package com.example.adminfoodgasm.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager.LayoutParams

fun Dialog.fixLayout() {
    val screenWidth = (context.resources.displayMetrics.widthPixels * 0.80).toInt()
    window?.setLayout(
        screenWidth,
        LayoutParams.WRAP_CONTENT
    )
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    window?.addFlags(LayoutParams.FLAG_DIM_BEHIND)
}

fun Float?.getProductPrice(price: Float): Float{
    //this --> Percentage
    if (this == null)
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}