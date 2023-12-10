package com.tech.codelab.blurimage.data

import androidx.annotation.StringRes

data class BlurAmount(
    @StringRes val blurAmountRes: Int,
    val blurAmount: Int
)