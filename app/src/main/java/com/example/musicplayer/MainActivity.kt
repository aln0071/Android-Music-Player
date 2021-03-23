package com.example.musicplayer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var x1 = 0.0;
    private var x2 = 0.0;
    private var minDistance = 150;
    private var detector:GestureDetector? = null
    private var songs = arrayOf<File>()
    private var songIndex = 0;

    private fun playSong() {
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(this.songs[this.songIndex].absolutePath)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        val textView = findViewById<TextView>(R.id.text_home)
        textView.text = this.songs[this.songIndex].name
    }

    private fun playNextSong() {
        this.songIndex++;
        if(this.songs.size <= this.songIndex) {
            this.songIndex = 0;
        }
        this.playSong()
    }

    private fun playPreviousSong() {
        this.songIndex--;
        if(this.songIndex < 0) {
            this.songIndex = this.songs.size-1;
        }
        this.playSong();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val mediadir = getExternalFilesDir("media")
        this.songs = mediadir?.listFiles()!!
        if(this.songs.isNotEmpty()) {
            this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(this.songs[songIndex]))
            for(f:File in this.songs) {
                Log.d("fileslist", f.name)
            }
        }

        detector = GestureDetector(this, GestureTap())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            // Do something
            mediaPlayer?.pause()
        }
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mediaPlayer?.start()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        detector?.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> x1 = event!!.x.toDouble()
            MotionEvent.ACTION_UP -> {
                x2 = event!!.x.toDouble()
                val deltaX = (x2 - x1).toFloat()
                if (deltaX > minDistance) {
//                    Log.d("path finder", externalCacheDir?.absolutePath!!)
                    Log.d("path finder", "")
                    Toast.makeText(this, "left2right swipe " + deltaX, Toast.LENGTH_SHORT).show()
                    this.playSong()
                } else if (deltaX < -minDistance) {
                    // consider as something else - a screen tap for example
                    Toast.makeText(this, "right to left swipe " + deltaX, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    inner class GestureTap : SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.i("onDoubleTap :", "" + e.action)
            System.out.println("double tap")
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            Log.i("onSingleTap :", "" + e.action)
            System.out.println("Single Tap")
            playSong()
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            System.out.println("x: " + distanceX.toInt() + " y: " + distanceY.toInt())
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            playNextSong()
                            Log.d("swipe", "next song")
                        } else {
                            playPreviousSong()
                        }
                        result = true
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
//                        onSwipeBottom()
                    } else {
//                        onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }

}