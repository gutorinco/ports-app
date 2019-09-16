package br.com.suitesistemas.portsmobile.custom.string

import br.com.suitesistemas.portsmobile.model.Phone

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