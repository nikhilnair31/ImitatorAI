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
import com.sil.imitatorai.TargetsAdapter
import kotlinx.android.synthetic.main.homepage_activity.*
import java.util.*

/**
 *
 */
class HomepageActivity : AppCompatActivity() {

    val TAG = "HomepageActivity"
    private val gson: Gson = Gson()
    private lateinit var prefalllist: MutableList<HashMap<String, String>>

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage_activity)

        recyclerview.layoutManager = LinearLayoutManager(this)

        initiateAdapter()
        checkIfListEmpty()

        notification_layout.setOnClickListener {
            openNotificationAccess()
        }
        give_permission_layout.setOnClickListener {
            openNotificationAccess()
        }
        add_target.setOnClickListener {
            val intent = Intent(this, AddTargetActivity::class.java)
            intent.putExtra("IS_UPDATE_MESSAGE", false)
            startActivityForResult(intent, 1001)
        }
        info_button.setOnClickListener {
            val intent1 = Intent(this, AboutPopup::class.java)
            startActivity(intent1)
        }

        checkIfPermissionGiven()
    }

    private fun initiateAdapter() {
        val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
        val listType = object : TypeToken<MutableList<HashMap<String, String>>>() {}.type
        prefalllist = gson.fromJson(prefs.all["data"] as String, listType)
        Log.d(TAG, "og prefalllist: $prefalllist")
        recyclerview.adapter = TargetsAdapter(prefalllist, this) { item -> doClick(item) }
    }

    private fun checkIfListEmpty() {
        if (prefalllist.isNotEmpty()) {
            no_target.visibility = View.GONE
        } else {
            no_target.visibility = View.VISIBLE
        }
    }

    private fun checkIfPermissionGiven() {
        if (isNotificationServiceRunning()) {
            add_target.visibility = View.VISIBLE
            botto_layout.visibility = View.GONE
            notification_layout.setCompoundDrawablesWithIntrinsicBounds(
                getDrawable(R.drawable.notification_turned_on), null, null, null
            )
        } else {
            add_target.visibility = View.GONE
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
        val intents = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivityForResult(intents, 100)
    }

    private fun readData() {
        prefalllist.clear()
        val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
        val listType = object : TypeToken<MutableList<HashMap<String, String>>>() {}.type
        prefalllist = gson.fromJson(prefs.all["data"] as String, listType)
        if (prefalllist.isNotEmpty()) {
            recyclerview.adapter = TargetsAdapter(prefalllist, this) { item -> doClick(item) }
        }
    }

    private fun doClick(item: HashMap<String, String>) {
        val intent = Intent(this, AddTargetActivity::class.java)
        intent.putExtra("IS_UPDATE_MESSAGE", true)
        intent.putExtra("DATA", item)
        startActivityForResult(intent, 1001)
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
}