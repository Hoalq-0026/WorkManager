package com.tech.codelab.blurimage

import android.app.Application
import com.tech.codelab.blurimage.data.AppContainer
import com.tech.codelab.blurimage.data.DefaultAppContainer

class BlurApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}