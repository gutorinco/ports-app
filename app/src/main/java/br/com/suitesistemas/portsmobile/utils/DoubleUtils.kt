package br.com.suitesistemas.portsmobile.utils

import br.com.suitesistemas.portsmobile.custom.extensions.formattedIntegerNumber

class DoubleUtils {

    fun toStringFormat(value: Double) : String {
        val formattedValue = getFormattedValue(value.toString())
        return formatParts(formattedValue)
    }

    fun getFormattedValue(textValue : String) : String {
        val value = textValue.toDouble()
        return when {
            value >= 1000 && value < 10000 -> textValue.formattedIntegerNumber(1)
            value >= 10000 && value < 100000 -> textValue.formattedIntegerNumber(2)
            value >= 100000 && value < 1000000 -> textValue.formattedIntegerNumber(3)
            else -> textValue.replace(".", ",")
        }
    }

    fun formatParts(formattedValue: String) : String {
        val parts = formattedValue.split(",")
        val value = if (parts.size == 2 && parts[1].isNotEmpty()) {
            val decimals = parts[1]
            if (decimals.length == 1) {
                decimals[0] + "0"
            } else {
                decimals[0] + "" + decimals[1]
            }
        } else {
            "00"
        }

        var integer = parts[0]
        if (integer.isEmpty())
            integer = "00"

        return "$integer,$value"
    }

}