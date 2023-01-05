package com.sil.imitatorai.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sil.imitatorai.BuildConfig
import com.sil.imitatorai.R
import kotlinx.android.synthetic.main.about_popup.*
import kotlinx.android.synthetic.main.splash_screen_activity.*

/**
 *
 */
class AboutPopup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_popup)

        val shader = LinearGradient(
            0f,
            0f,
            about_header.textSize * 3,
            0f,
            getColor(R.color.start_grad),
            getColor(R.color.end_grad),
            Shader.TileMode.CLAMP
        )
        about_header.paint.shader = shader

        close_dialog_button.setOnClickListener {
            finish()
        }

        share_app.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Be a dick to everyone and have them text with a bot with : " + getString(R.string.play_store_link) + BuildConfig.APPLICATION_ID
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        github_link.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(getString(R.string.github_link))
            startActivity(openURL)
        }
        playstore_link.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(getString(R.string.play_store_link))
            startActivity(openURL)
        }

    }


}