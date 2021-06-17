package com.sil.imitatorai.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sil.imitatorai.R
import kotlinx.android.synthetic.main.homepage_activity.*
import kotlinx.android.synthetic.main.homepage_activity.next_btn
import kotlinx.android.synthetic.main.intro_activity.*

/**
 *
 */
class IntroActivity : AppCompatActivity() {

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)

        window.statusBarColor = ContextCompat.getColor(this, R.color.header_color)
        val animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate)
        ball_image.startAnimation(animationRotate)

        next_btn.setOnClickListener {
            val prefs = getSharedPreferences("flags", Context.MODE_PRIVATE)
            prefs.edit().putBoolean(
                "showIntro", true
            ).apply()
            val intent = Intent(applicationContext, HomepageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}