package com.example.courses.core.data.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody

class MockInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // ИСПРАВЛЕНО: Легальный и чистый доступ к пути URL в OkHttp3 без toUri()
        val path = chain.request().url().encodedPath()
        
        return if (path.endsWith("api/courses")) {
            val jsonString = try {
                context.assets.open("courses.json").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                "{\"courses\":[]}"
            }

            val mediaType = MediaType.parse("application/json")
            val responseBody = ResponseBody.create(mediaType, jsonString)

            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("OK")
                .body(responseBody)
                .build()
        } else {
            chain.proceed(chain.request())
        }
    }
}
