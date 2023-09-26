package com.example.mysmsreadapp

import lombok.Data
import java.sql.Timestamp

@Data
data class SMSData (
    val id: Long,
    val sender: String,
    val body: String,
    val date: Timestamp,
    val type: Int,

        )