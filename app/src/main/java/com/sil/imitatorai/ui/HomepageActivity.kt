package com.sil.imitatorai.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sil.imitatorai.R
import com.sil.imitatorai.ReplyMessageAdapter
import com.sil.imitatorai.models.SaveCustomeMessage
import io.realm.Realm
import kotlinx.android.synthetic.main.create_target_activity.*
import kotlinx.android.synthetic.main.homepage_activity.*
import java.util.*

/**
 *
 */
class HomepageActivity : AppCompatActivity() {

    private val gson: Gson = Gson()
    private lateinit var realm: Realm
    private lateinit var list: ArrayList<SaveCustomeMessage>

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage_activity)

        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.commitTransaction()

        recyclerview.layoutManager = LinearLayoutManager(this)

        initiateAdapter()
        checkIfListEmpty()

        notification_layout.setOnClickListener {
            openNotificationAccess()
        }
        give_permission_layout.setOnClickListener {
            openNotificationAccess()
        }
        create_msg.setOnClickListener {
            val intent = Intent(this, AddTargetActivity::class.java)
            intent.putExtra(IS_UPDATE_MESSAGE, false)
            startActivityForResult(intent, Companion.REQUEST_CODE_DATA)
        }
        info_button.setOnClickListener {
            val intent1 = Intent(this, AboutPopup::class.java)
            startActivity(intent1)
        }

        checkIfPermissionGiven()
    }

    @SuppressLint("LogConditional")
    private fun initiateAdapter() {
        val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
        val listType = object : TypeToken<MutableList<HashMap<String, String>>>() {}.type
        val prefalllist: MutableList<HashMap<String, String>> =
            gson.fromJson(prefs.all["data"] as String, listType)
        Log.d("og prefalllist", prefalllist.toString())
        // recyclerview.adapter = TargetsAdapter(prefalllist, this) { item -> doClick(item) }

        list = ArrayList(realm.where(SaveCustomeMessage::class.java).findAll())
        Log.d("rlist", list.toString())
        recyclerview.adapter = ReplyMessageAdapter(list, this) { item -> doClick(item) }
    }

    private fun checkIfListEmpty() {
        if (list.isNotEmpty()) {
            no_target.visibility = View.GONE
        } else {
            no_target.visibility = View.VISIBLE
        }
    }

    private fun checkIfPermissionGiven() {
        if (isNotificationServiceRunning()) {
            create_msg.visibility = View.VISIBLE
            botto_layout.visibility = View.GONE
            notification_layout.setCompoundDrawablesWithIntrinsicBounds(
                getDrawable(R.drawable.notification_turned_on), null, null, null
            )
        } else {
            create_msg.visibility = View.GONE
            botto_layout.visibility = View.VISIBLE
            notification_layout.setCompoundDrawablesWithIntrinsicBounds(
                getDrawable(R.drawable.notification_not_on), null, null, null
            )
        }
    }

    private fun isNotificationServiceRunning(): Boolean {
        val map = NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
            .filterIndexed { _, value -> value == packageName }
        return map.size == 1
    }

    private fun openNotificationAccess() {
        //Open listener reference message // Notification access
        val intents = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivityForResult(intents, 100)
    }

    private fun readData() {
        list.clear()
        list = ArrayList(realm.where(SaveCustomeMessage::class.java).findAll())
        if (list.isNotEmpty()) {
            recyclerview.adapter = ReplyMessageAdapter(list, this) { item -> doClick(item) }
        }
    }

    /**
     *
     */
    private fun doClick(item: SaveCustomeMessage) {
        val intent = Intent(this, AddTargetActivity::class.java)
        intent.putExtra(IS_UPDATE_MESSAGE, true)
        intent.putExtra("DATA", item)
        startActivityForResult(intent, Companion.REQUEST_CODE_DATA)
    }

    /**
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkIfPermissionGiven()
        readData()
        checkIfListEmpty()
    }

    /**
     *
     */
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    companion object {
        const val REQUEST_CODE_DATA = 1001
        const val IS_UPDATE_MESSAGE = "IS_UPDATE_MESSAGE"
    }
}