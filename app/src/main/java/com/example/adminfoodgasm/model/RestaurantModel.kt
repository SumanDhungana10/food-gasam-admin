package com.example.adminfoodgasm.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RestaurantModel(
    var id: String? = null,
    var category: String? = null,
    var name: String? = null,
    var address: String? = null,
    var duration: String? = null,
    var discount: String? = null,
    var price: Double? = null,
    var image: String? = null,
    var offerPercentage : Float? = null
) : Parcelable