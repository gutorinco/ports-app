package br.com.suitesistemas.portsmobile.custom.extensions

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import br.com.suitesistemas.portsmobile.view.adapter.SpinnerAdapter

fun Spinner.onItemSelected(selected: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selected.invoke(position)
        }
    }
}

fun Spinner.setAdapterAndSelection(context: Context, names: List<String>, index: Int? = null) {
    adapter = SpinnerAdapter(context, names)
    setSelection(index ?: -1)
}