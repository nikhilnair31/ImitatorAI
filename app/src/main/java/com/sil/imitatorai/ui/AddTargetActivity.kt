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
import com.sil.imitatorai.models.SaveCustomeMessage
import io.realm.Realm
import kotlinx.android.synthetic.main.create_target_activity.*


/**
 *
 */
class AddTargetActivity : AppCompatActivity() {

    private val gson: Gson = Gson()
    private lateinit var realm: Realm
    private var isUpdateMessage = false
    private var messageId = ""

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.create_target_activity)

        isUpdateMessage = intent.getBooleanExtra(HomepageActivity.IS_UPDATE_MESSAGE, false)
        if (intent != null && intent.getParcelableExtra<SaveCustomeMessage>("DATA") != null && isUpdateMessage) {
            updateMessage(intent.getParcelableExtra("DATA") ?: return)
            delete_button.visibility = View.VISIBLE
            messageId = (intent.getParcelableExtra<SaveCustomeMessage>("DATA")
                ?: return).expectedMessage.toString()
        } else {
            delete_button.visibility = View.GONE
        }

        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.commitTransaction()

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
        Log.d("og prefalllist", prefalllist.toString())

        val foundMap =
            prefalllist.find { user -> user.containsValue(target_name_edittext.text.toString()) }
        Log.d("foundMap", foundMap.toString())
        val foundMapAt = prefalllist.indexOf(foundMap)
        Log.d("foundMapAt", foundMapAt.toString())
        if (foundMapAt != -1) {
            prefalllist.removeAt(prefalllist.indexOf(foundMap))
            Log.d("fin prefalllist", prefalllist.toString())
            prefs.edit().putString(
                "data", gson.toJson(prefalllist)
            ).apply()
        } else {
            Toast.makeText(this, "Delete F", Toast.LENGTH_SHORT).show()
        }

        val msgs = realm.where(SaveCustomeMessage::class.java).findAll()
        val userdatabase =
            msgs.where().equalTo("expectedMessage", target_name_edittext.text.toString())
                .equalTo("replyMessage", reply_rate_edittext.text.toString()).findFirst()
        if (userdatabase != null) {
            if (!realm.isInTransaction) {
                realm.beginTransaction()
            }
            userdatabase.deleteFromRealm()
            realm.commitTransaction()
        }
        finish()
    }

    private fun initEditText() {
        if (isUpdateMessage) {
            //SharedPrefs attempt
            val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
            val listType = object : TypeToken<MutableList<HashMap<String, String>>>() {}.type

            val prefalllist: MutableList<HashMap<String, String>> =
                gson.fromJson(prefs.all["data"] as String, listType)
            Log.d("og prefalllist", prefalllist.toString())

            prefalllist.add(
                hashMapOf(
                    "targetname" to target_name_edittext.text.toString(),
                    "replyrate" to reply_rate_edittext.text.toString()
                )
            )
            Log.d("fin prefalllist", prefalllist.toString())

            prefs.edit().putString(
                "data", gson.toJson(prefalllist)
            ).apply()

            header.text = getString(R.string.update)
            realm.executeTransaction {
                val saveCustomeMessage =
                    it.where(SaveCustomeMessage::class.java).equalTo("expectedMessage", messageId)
                        .findFirst()
                saveCustomeMessage?.expectedMessage = target_name_edittext.text?.toString()
                saveCustomeMessage?.replyMessage = reply_rate_edittext.text?.toString()
            }
            finish()

        } else {
            //SharedPrefs attempt
            val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
            val listType = object : TypeToken<MutableList<HashMap<String, String>>>() {}.type

            val prefalllist: MutableList<HashMap<String, String>> = if (prefs.all["data"] != null) {
                gson.fromJson(prefs.all["data"] as String, listType)
            } else {
                mutableListOf()
            }
            Log.d("og prefalllist", prefalllist.toString())

            prefalllist.add(
                hashMapOf(
                    "targetname" to target_name_edittext.text.toString(),
                    "replyrate" to reply_rate_edittext.text.toString()
                )
            )
            Log.d("fin prefalllist", prefalllist.toString())

            prefs.edit().putString(
                "data", gson.toJson(prefalllist)
            ).apply()

            header.text = getString(R.string.create)
            realm.executeTransactionAsync({
                val message = it.createObject(SaveCustomeMessage::class.java)
                message.expectedMessage = target_name_edittext.text.toString()
                message.replyMessage = reply_rate_edittext.text.toString()

            }, {
                Log.d("Save Success", "On Success: Data Written Successfully!")
                finish()
                clearEditText()
            }, {
                Log.d("Save Success", "On Error: Error in saving Data!")
            })
        }
    }

    private fun showError() {
        Toast.makeText(this, "showError", Toast.LENGTH_SHORT).show()
        Log.d("showError", "showError")
    }

    private fun checkError(): Boolean {
        return !target_name_edittext.text?.isEmpty()!! && !reply_rate_edittext.text?.isEmpty()!!
    }

    private fun updateMessage(saveCustomeMessage: SaveCustomeMessage) {
        target_name_edittext.setText(saveCustomeMessage.expectedMessage)
        reply_rate_edittext.setText(saveCustomeMessage.replyMessage)
    }

    private fun clearEditText() {
        target_name_edittext.setText("")
        reply_rate_edittext.setText("")
    }
}