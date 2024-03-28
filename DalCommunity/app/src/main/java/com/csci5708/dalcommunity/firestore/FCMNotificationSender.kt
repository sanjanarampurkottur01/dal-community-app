package com.csci5708.dalcommunity.firestore

import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.dalcommunity.R
import com.google.auth.oauth2.ServiceAccountCredentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object FCMNotificationSender {

    fun sendNotification(targetToken: String, title: String, message: String, context: Context, accessToken: String, priority: String) {
        val client = OkHttpClient()

        try {
            val json = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", targetToken)
                    put("notification", JSONObject().apply {
                        put("title", title)
                        put("body", message)
//                        put("android", JSONObject().apply {
//                            put("visibility", NotificationCompat.VISIBILITY_PUBLIC)
//                        })
                    })
                    put("data", JSONObject().apply {
                        put("title", title)
                        put("message", message)
                    })
//                    put("android", JSONObject().apply {
//                        put("priority", priority) // Set priority here
//                    })
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/dal-community-01/messages:send")
                .post(requestBody)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                println(response.body!!.string())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun sendNotificationToMultipleUsers(targetTokens: List<String>, title: String, message: String, context: Context, accessToken: String, priority: String) {
        val client = OkHttpClient()
        try {
            for (targetToken in targetTokens) {
                val json = JSONObject().apply {
                    put("message", JSONObject().apply {
                        put("token", targetToken)
                        put("notification", JSONObject().apply {
                            put("title", title)
                            put("body", message)
                        })
                        put("data", JSONObject().apply {
                            put("title", title)
                            put("message", message)
                        })
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("https://fcm.googleapis.com/v1/projects/dal-community-01/messages:send")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    println(response.body!!.string())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getAccessToken(context: Context): String {
        try {
            val RAW_RESOURCE_ID = R.raw.dalcommunitysecret // Replace with your raw resource ID
            val inputStream = context.resources.openRawResource(RAW_RESOURCE_ID)
            val credentials = ServiceAccountCredentials
                .fromStream(inputStream)
                .createScoped("https://www.googleapis.com/auth/firebase.messaging")
            return credentials.refreshAccessToken().tokenValue
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error getting access token: ${e.message}")
        }
    }
}