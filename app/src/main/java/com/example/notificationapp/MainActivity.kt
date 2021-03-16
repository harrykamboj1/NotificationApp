package com.example.notificationapp

import android.app.Notification
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.notificationapp.FirebaseNotifications.NotificationData
import com.example.notificationapp.FirebaseNotifications.PushNotification
import com.example.notificationapp.FirebaseNotifications.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


const val TOPIC = "/topics/myTopic"
class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        btnNotification.setOnClickListener {
            val title = etTitle.text.toString()
            val message = etMessage.text.toString()

            if(title.isNotEmpty() && message.isNotEmpty()){
                 PushNotification(
                         NotificationData(title,message),
                         TOPIC
                 ).also {
                     sendNotifications(it)
                 }
            }
        }
    }

    private fun sendNotifications(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
               response?.body().let {
                   Gson().toJson(response)
               }
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}