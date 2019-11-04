package br.com.suitesistemas.portsmobile.custom.extensions

import br.com.suitesistemas.portsmobile.model.Phone
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import java.text.Normalizer

fun String.removeWhitespaces(): String {
    return replace("\\s".toRegex(), "")
}

fun String.getPhoneNumber(): Phone? {
    val value = removeWhitespaces().replace("-", "").replace("(", "").replace(")", "").replace("null", "")
    return when {
        value.length == 11 -> Phone(value.substring(0, 2), value.substring(2, 11))
        value.length == 10 -> Phone(value.substring(0, 2), value.substring(2, 10))
        value.isNotEmpty() -> null
        else -> Phone("", "")
    }
}

fun String.unaccent(): String {
    val regexUnaccent = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return regexUnaccent.replace(temp, "")
}

fun String.numbersOnly(): String {
    val regex = Regex("[^A-Za-z0-9 ]")
    val text = this.removeWhitespaces()
    return text.replace(regex, "")
}

fun String.removeLetters(): String {
    val regex = Regex("[A-Za-z]")
    val text = this.removeWhitespaces()
    return text.replace(regex, "")
}

fun String.formattedIntegerNumber(start: Int) : String {
    val beforeDot = this.substring(0, start)
    val afterDot = this.substring(start, this.length).replace(".", ",")
    return "$beforeDot.$afterDot"
}

fun String.toDoubleValue() : Double {
    var value = 0.0
    if (isNotEmpty())
        value = DoubleUtils().toStringFormat(replace(".", "").replace(",", ".").toDouble()).replace(",", ".").toDouble()
    return value
}