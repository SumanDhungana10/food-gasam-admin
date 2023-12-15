package com.example.adminfoodgasm.model

import com.google.gson.annotations.SerializedName

data class KhaltiValidationErrorResponse(

	@field:SerializedName("token")
	val token: List<String?>? = null
)
