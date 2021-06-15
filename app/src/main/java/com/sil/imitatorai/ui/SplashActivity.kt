package com.sil.imitatorai.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.sil.imitatorai.R

/**
 *
 */
class SplashActivity : AppCompatActivity() {

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(applicationContext, HomepageActivity::class.java)
            startActivity(intent)
            finish()
        }, 500)
    }
}