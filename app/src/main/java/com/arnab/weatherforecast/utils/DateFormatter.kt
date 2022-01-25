package com.arnab.weatherforecast.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    fun getFormattedDate(millisecond: Long): String {
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val simpleTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return simpleDateFormat.format(millisecond) + " - " + simpleTimeFormat.format(millisecond)
    }
}