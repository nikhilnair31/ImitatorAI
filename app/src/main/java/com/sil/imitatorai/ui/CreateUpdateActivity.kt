package com.sil.imitatorai.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sil.imitatorai.R
import com.sil.imitatorai.models.SaveCustomeMessage
import io.realm.Realm
import kotlinx.android.synthetic.main.create_update_activity.*


/**
 *
 */
class CreateUpdateActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private var isUpdateMessage = false
    private var messageId = ""

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.create_update_activity)

        isUpdateMessage = intent.getBooleanExtra(CustomizeMessageActivity.IS_UPDATE_MESSAGE, false)
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

    private fun showError() {
        target_name.isErrorEnabled = true
        reply_rate.isErrorEnabled = true
    }

    private fun checkError(): Boolean {
        return !target_name_edittext.text?.isEmpty()!! && !reply_rate_edittext.text?.isEmpty()!!
    }

    private fun deleteMessage() {
        val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
        prefs.edit().remove(target_name_edittext.text.toString()).apply()

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

    private fun updateMessage(saveCustomeMessage: SaveCustomeMessage) {
        target_name_edittext.setText(saveCustomeMessage.expectedMessage)
        reply_rate_edittext.setText(saveCustomeMessage.replyMessage)
    }

    private fun initEditText() {
        if (isUpdateMessage) {
            //SharedPrefs attempt
            val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
            prefs.edit().putString(
                target_name_edittext.text.toString(),
                reply_rate_edittext.text.toString()
            ).apply()

            header.text = getString(R.string.update)
            realm.executeTransaction {
                var saveCustomeMessage =
                    it.where(SaveCustomeMessage::class.java).equalTo("expectedMessage", messageId)
                        .findFirst()
                saveCustomeMessage?.expectedMessage = target_name_edittext.text?.toString()
                saveCustomeMessage?.replyMessage = reply_rate_edittext.text?.toString()
            }
            finish()

        } else {
            //SharedPrefs attempt
            val prefs = getSharedPreferences("test", Context.MODE_PRIVATE)
            prefs.edit().putString(
                target_name_edittext.text.toString(),
                reply_rate_edittext.text.toString()
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

    private fun clearEditText() {
        target_name_edittext.setText("")
        reply_rate_edittext.setText("")
    }

    /**
     *
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}