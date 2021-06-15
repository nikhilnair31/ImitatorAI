package com.sil.imitatorai.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sil.imitatorai.BuildConfig
import com.sil.imitatorai.R
import kotlinx.android.synthetic.main.about_popup.*

/**
 *
 */
class AboutPopup : AppCompatActivity() {

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_popup)

        close_dialog_button.setOnClickListener { finish() }

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

    }


}