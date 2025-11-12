package com.example.studyktflow

import android.app.Application
import com.example.studyktflow.data.network.RetrofitClient

class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(this)
    }
}
