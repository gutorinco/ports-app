package br.com.suitesistemas.portsmobile.custom.extensions

import android.view.View
import android.widget.ProgressBar

fun ProgressBar.show() {
    isIndeterminate = true
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    isIndeterminate = false
    visibility = View.INVISIBLE
}