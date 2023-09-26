package com.example.mysmsreadapp

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SmsApi {
    @POST("sms")
    fun saveSMSData(@Body smsList: MutableList<SMSData>): Call<Void>
}