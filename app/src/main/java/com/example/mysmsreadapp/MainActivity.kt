package com.example.mysmsreadapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mysmsreadapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val PERMISSIONS_REQUEST_READ_SMS = 100
        private const val BASE_URL = "https://pambackend-pamapi.azuremicroservices.io/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //For Button
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.smsButton.setOnClickListener{
            // 버튼 클릭 시
            loadSMSData()
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), PERMISSIONS_REQUEST_READ_SMS)
        } else {
            loadSMSData()
        }
    }

    private var smsList: List<SMSData> = emptyList()

    private fun loadSMSData() {
        val uri = Uri.parse("content://sms/")
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.TYPE
        )
        val sortOrder = "${Telephony.Sms.DATE} DESC"

        val cursor = contentResolver.query(uri, projection, null, null, sortOrder)

        if (cursor != null && cursor.moveToFirst()) {

            val smsDataList: MutableList<SMSData> = mutableListOf()
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms._ID))
                val sender = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val longTypedate = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val date = Timestamp(longTypedate)

                val type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))

                val smsData = SMSData(id, sender, body, date, type)
                smsDataList.add(smsData)
            } while (cursor.moveToNext())
            smsList =smsDataList
        }

        cursor?.close()

        sendSMSDataToServer()
    }


    private fun sendSMSDataToServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val smsApi = retrofit.create(SmsApi::class.java)
        smsApi.saveSMSData(smsList as MutableList<SMSData>).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                // 서버 응답 처리
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 서버 요청 실패 처리
            }
        })
    }
}