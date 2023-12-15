package com.example.adminfoodgasm.model

import com.google.gson.annotations.SerializedName

data class KhaltiValidationSuccessResponse(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("fee_amount")
	val feeAmount: Int? = null,

	@field:SerializedName("ebanker")
	val ebanker: Any? = null,

	@field:SerializedName("created_on")
	val createdOn: String? = null,

	@field:SerializedName("merchant")
	val merchant: Merchant? = null,

	@field:SerializedName("refunded")
	val refunded: Boolean? = null,

	@field:SerializedName("state")
	val state: State? = null,

	@field:SerializedName("idx")
	val idx: String? = null,

	@field:SerializedName("type")
	val type: Type? = null,

	@field:SerializedName("user")
	val user: User? = null
)

data class User(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("idx")
	val idx: String? = null
)

data class Type(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("idx")
	val idx: String? = null
)

data class State(

	@field:SerializedName("template")
	val template: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("idx")
	val idx: String? = null
)

data class Merchant(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("idx")
	val idx: String? = null
)
