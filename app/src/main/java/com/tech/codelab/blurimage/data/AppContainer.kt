package com.tech.codelab.blurimage.data

import android.content.Context


interface AppContainer {
    val blurRepository: BlurRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val blurRepository: BlurRepository = WorkManagerBlurRepository(context)

}