package com.csci5708.dalcommunity.firestore

import com.google.auth.oauth2.ServiceAccountCredentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.FileInputStream
import java.io.IOException

object FCMNotificationSender {

    fun sendNotification(targetToken: String, title: String, message: String) {
        val client = OkHttpClient()
        val json = JSONObject()
        val body = JSONObject()

        try {
            body.put("title", title)
            body.put("body", message)

            json.put("message", JSONObject().apply {
                put("token", targetToken)
                put("notification", body)
            })

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/dal-community-01/messages:send")
                .post(requestBody)
                .addHeader("Authorization", "Bearer ${getAccessToken()}")
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

    private fun getAccessToken(): String {
        try {
            val serviceAccountKeyFile = "./app/google-services.json"
//            val credentials = GoogleCredentials.fromStream(FileInputStream(serviceAccountKeyFile))
//                .createScoped("https://www.googleapis.com/auth/firebase.messaging")
            val credentials = ServiceAccountCredentials
                .fromStream(FileInputStream(serviceAccountKeyFile))
                .createScoped("https://www.googleapis.com/auth/firebase.messaging")
            return credentials.refreshAccessToken().tokenValue
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error getting access token: ${e.message}")
        }
    }
}