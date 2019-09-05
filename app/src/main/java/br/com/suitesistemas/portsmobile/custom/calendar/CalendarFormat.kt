package br.com.suitesistemas.portsmobile.custom.calendar

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toStringFormat(format: String = "dd/MM/yyyy", date: Date = this.time): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(date)
}

fun Date.toStringFormat(format: String = "dd/MM/yyyy"): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}