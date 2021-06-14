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
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(applicationContext, CustomizeMessageActivity::class.java)
            startActivity(intent)
        }, 500)
    }
}