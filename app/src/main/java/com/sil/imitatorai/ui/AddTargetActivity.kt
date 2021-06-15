package com.sil.imitatorai.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sil.imitatorai.R
import kotlinx.android.synthetic.main.create_target_activity.*
import java.util.*

/**
 *
 */
class AddTargetActivity : AppCompatActivity() {

    val TAG = "AddTargetActivity"
    private val gson: Gson = Gson()
    private var isUpdateMessage = false
    private var messageId = ""

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.create_target_activity)

        isUpdateMessage = intent.getBooleanExtra("IS_UPDATE_MESSAGE", false)
        if (intent != null && isUpdateMessage) {
            val ser = intent.getSerializableExtra("DATA") as HashMap<String, String>
            updateMessage(ser)
            delete_button.visibility = View.VISIBLE
            messageId = ser["targetname"] ?: return
        } else {
            delete_button.visibility = View.GONE
        }

        done_button.setOnClickListener {
            if (checkError()) {
                initEditText()
            } else {
                showError()
            }
        }
        delete_button.setOnClickListener {
            deleteMessage()
        }
        close_button.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("LogConditional")
    private fun deleteMessage() {
        val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
        val listType = object : TypeToken<MutableList<HashMap<String, String>>>() {}.type

        val prefalllist: MutableList<HashMap<String, String>> =
            gson.fromJson(prefs.all["data"] as String, listType)
        Log.d(TAG, "og prefalllist: $prefalllist")

        val foundMap =
            prefalllist.find { user -> user.containsValue(target_name_edittext.text.toString()) }
        Log.d(TAG, "foundMap: $foundMap")
        val foundMapAt = prefalllist.indexOf(foundMap)
        Log.d(TAG, "foundMapAt: $foundMapAt")
        if (foundMapAt != -1) {
            prefalllist.removeAt(prefalllist.indexOf(foundMap))
            Log.d(TAG, "fin prefalllist: $prefalllist")
            prefs.edit().putString(
                "data", gson.toJson(prefalllist)
            ).apply()
        } else {
            Toast.makeText(this, "Delete F", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun initEditText() {
        val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
        val listType = object : TypeToken<MutableList<HashMap<String, String>>>() {}.type
        if (isUpdateMessage) {
            //SharedPrefs attempt
            val prefalllist: MutableList<HashMap<String, String>> =
                gson.fromJson(prefs.all["data"] as String, listType)
            Log.d(TAG, "og prefalllist\": $prefalllist")

            prefalllist.add(
                hashMapOf(
                    "targetname" to target_name_edittext.text.toString(),
                    "replyrate" to reply_rate_edittext.text.toString()
                )
            )
            Log.d(TAG, "fin prefalllist\": $prefalllist")

            prefs.edit().putString(
                "data", gson.toJson(prefalllist)
            ).apply()

        }
        else {
            //SharedPrefs attempt
            val prefalllist: MutableList<HashMap<String, String>> = if (prefs.all["data"] != null) {
                gson.fromJson(prefs.all["data"] as String, listType)
            } else {
                mutableListOf()
            }
            Log.d(TAG, "og prefalllist\": $prefalllist")

            prefalllist.add(
                hashMapOf(
                    "targetname" to target_name_edittext.text.toString(),
                    "replyrate" to reply_rate_edittext.text.toString()
                )
            )
            Log.d(TAG, "fin prefalllist\": $prefalllist")

            prefs.edit().putString(
                "data", gson.toJson(prefalllist)
            ).apply()

        }
        finish()
    }

    private fun showError() {
        Toast.makeText(this, "showError", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "showError")
    }

    private fun checkError(): Boolean {
        return !target_name_edittext.text?.isEmpty()!! && !reply_rate_edittext.text?.isEmpty()!!
    }

    private fun updateMessage(data: HashMap<String, String>) {
        target_name_edittext.setText(data["targetname"])
        reply_rate_edittext.setText(data["replyrate"])
    }
}