package com.example.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

internal class GestureTap : SimpleOnGestureListener() {
    override fun onDoubleTap(e: MotionEvent): Boolean {
        Log.i("onDoubleTap :", "" + e.action)
        System.out.println("double tap")
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        Log.i("onSingleTap :", "" + e.action)
        System.out.println("Single Tap")
        return true
    }

//    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//        System.out.println("x: "+ distanceX.toInt() +" y: "+ distanceY.toInt())
//        return super.onScroll(e1, e2, distanceX, distanceY)
//    }
}

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var x1 = 0.0;
    private var x2 = 0.0;
    private var minDistance = 150;
    private var detector:GestureDetector? = null

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

        val mydir: File = this.getDir("users", Context.MODE_PRIVATE) //Creating an internal dir;

        if (!mydir.exists()) {
            mydir.mkdirs()
            Log.d("path finder", "creating directory")
        } else {
            Log.d("path finder", "dir exists")
            Log.d("path finder", mydir.absolutePath)
            Log.d("path finder", Environment.getDataDirectory().toString())
            Log.d("path finder", filesDir.absolutePath)
            Log.d("path finder", getExternalFilesDir("media").toString())
        }

        detector = GestureDetector(this, GestureTap())

//        var songUri = Uri.fromFile(externalCacheDir)
        mediaPlayer = MediaPlayer.create(this, R.raw.song)
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
                } else if (deltaX < -minDistance) {
                    // consider as something else - a screen tap for example
                    Toast.makeText(this, "right to left swipe " + deltaX, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onTouchEvent(event)
    }

}