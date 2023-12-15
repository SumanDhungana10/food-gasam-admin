package com.example.adminfoodgasm.utils

import com.example.adminfoodgasm.Constants
import com.example.adminfoodgasm.model.VerificationPayload
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkUtils {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://khalti.com/api/v2/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Key ${Constants.TEST_SECRET_KEY}")
                    .addHeader("Content-Type", "text/plain")
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    private val client by lazy {
        retrofit.create(ApiInterface::class.java)
    }

    suspend fun verifyPayment(payload: VerificationPayload) = client.verifyPayment(payload)
}