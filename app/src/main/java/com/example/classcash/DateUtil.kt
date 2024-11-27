package com.example.classcash

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    fun getCurrentDate(): String {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormatter.format(Date())
    }
}