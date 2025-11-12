package com.example.studyktflow.data.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(context: Context) : CookieJar {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("cookies", Context.MODE_PRIVATE)
    
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val editor = prefs.edit()
        cookies.forEach { cookie ->
            editor.putString(cookie.name, "${cookie.value}|${cookie.expiresAt}")
        }
        editor.apply()
    }
    
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = mutableListOf<Cookie>()
        prefs.all.forEach { (key, value) ->
            if (value is String) {
                val parts = value.split("|")
                if (parts.size >= 1) {
                    val cookieValue = parts[0]
                    cookies.add(Cookie.Builder()
                        .name(key)
                        .value(cookieValue)
                        .domain(url.host)
                        .build())
                }
            }
        }
        return cookies
    }
    
    fun clearCookies() {
        prefs.edit().clear().apply()
    }
}
