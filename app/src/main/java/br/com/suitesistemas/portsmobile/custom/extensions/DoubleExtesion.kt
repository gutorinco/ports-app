package br.com.suitesistemas.portsmobile.custom.extensions

import br.com.suitesistemas.portsmobile.utils.DoubleUtils

fun Double.toStringValue() : String {
    return DoubleUtils().toStringFormat(this)
}