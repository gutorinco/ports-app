package br.com.suitesistemas.portsmobile.custom.spinner

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

fun Spinner.onItemSelected(selected: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selected.invoke(position)
        }
    }
}