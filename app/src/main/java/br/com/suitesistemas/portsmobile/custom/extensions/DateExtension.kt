package br.com.suitesistemas.portsmobile.custom.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toStringFormat(format: String = "dd/MM/yyyy", date: Date = this.time): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(date)
}

fun Date.toStringFormat(format: String = "dd/MM/yyyy"): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}

fun Date.year(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.YEAR)
}

fun Date.month(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.MONTH)
}

fun Date.day(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.DAY_OF_MONTH)
}