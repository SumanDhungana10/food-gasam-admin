package com.example.adminfoodgasm.utils

import com.example.adminfoodgasm.model.KhaltiValidationSuccessResponse
import com.example.adminfoodgasm.model.VerificationPayload
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// API Interface
interface ApiInterface {
    @POST("payment/verify")
    suspend fun verifyPayment(@Body payload: VerificationPayload): Response<KhaltiValidationSuccessResponse>
}