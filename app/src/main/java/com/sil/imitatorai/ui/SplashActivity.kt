package com.sil.imitatorai.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.sil.imitatorai.R
import kotlinx.android.synthetic.main.homepage_activity.*
import kotlinx.android.synthetic.main.homepage_activity.app_title
import kotlinx.android.synthetic.main.splash_screen_activity.*

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

        val shader = LinearGradient(
            0f,
            0f,
            app_name_txt.textSize * 3,
            0f,
            getColor(R.color.start_grad),
            getColor(R.color.end_grad),
            Shader.TileMode.CLAMP
        )
        app_name_txt.paint.shader = shader

        val prefs = getSharedPreferences("flags", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("showIntro", false)) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(applicationContext, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(applicationContext, HomepageActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }
    }
}