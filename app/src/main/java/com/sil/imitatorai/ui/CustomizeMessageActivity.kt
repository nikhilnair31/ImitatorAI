package com.sil.imitatorai.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sil.imitatorai.R
import com.sil.imitatorai.ReplyMessageAdapter
import com.sil.imitatorai.models.SaveCustomeMessage
import io.realm.Realm
import kotlinx.android.synthetic.main.customize_message_activity.*

class CustomizeMessageActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    lateinit var list: ArrayList<SaveCustomeMessage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customize_message_activity)

        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.commitTransaction()

        recyclerview.layoutManager = LinearLayoutManager(this)

        initiateAdapter()
        checkIfListEmpty()
        // submit_button.setOnClickListener { initEditText() }

        notification_layout.setOnClickListener {
            openNotificationAccess()
        }
        give_permission_layout.setOnClickListener {
            openNotificationAccess()
        }
        create_msg.setOnClickListener {
            val intent = Intent(this, CreateUpdateActivity::class.java)
            intent.putExtra(IS_UPDATE_MESSAGE, false)
            startActivityForResult(intent, Companion.REQUEST_CODE_DATA)
        }
        info_button.setOnClickListener {
            val intent1 = Intent(this, AboutPopup::class.java)
             startActivity(intent1)
        }

        checkIfPermissionGiven()
    }

    private fun initiateAdapter() {
        list = ArrayList(realm.where(SaveCustomeMessage::class.java).findAll())
        recyclerview.adapter = ReplyMessageAdapter(list, this ) { item -> doClick(item) }
    }

    private fun checkIfListEmpty() {
        if (!list.isEmpty()) {
            no_reply.visibility = View.GONE
        }
        else {
            no_reply.visibility = View.VISIBLE
        }
    }

    private fun isNotificationServiceRunning(): Boolean {
        val map = NotificationManagerCompat.getEnabledListenerPackages(applicationContext).filterIndexed { index, value -> value == packageName }
        return map.size == 1
    }

    private fun openNotificationAccess() {
        //Open listener reference message // Notification access
        val intents = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivityForResult(intents, 100)
    }

    private fun checkIfPermissionGiven() {
        if (isNotificationServiceRunning()) {
            create_msg.visibility = View.VISIBLE
            botto_layout.visibility = View.GONE
            notification_layout.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.notification_turned_on), null, null, null)
        }
        else {
            create_msg.visibility = View.GONE
            botto_layout.visibility = View.VISIBLE
            notification_layout.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.notification_not_on), null, null, null)
        }
    }

    private fun readData() {
        list.clear()
        list = ArrayList(realm.where(SaveCustomeMessage::class.java).findAll())
        if (!list.isEmpty()) {
            recyclerview.adapter = ReplyMessageAdapter(list, this ) { item -> doClick(item) }
        }
    }

    fun doClick(item: SaveCustomeMessage) {
        val intent = Intent(this, CreateUpdateActivity::class.java)
        intent.putExtra(IS_UPDATE_MESSAGE, true)
        intent.putExtra("DATA", item)
        startActivityForResult(intent, Companion.REQUEST_CODE_DATA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkIfPermissionGiven()
        readData()
        checkIfListEmpty()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    companion object {
        const val REQUEST_CODE_DATA = 1001
        const val IS_UPDATE_MESSAGE = "IS_UPDATE_MESSAGE"
    }
}