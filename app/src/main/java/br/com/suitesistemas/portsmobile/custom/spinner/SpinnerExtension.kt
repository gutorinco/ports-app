package br.com.suitesistemas.portsmobile.custom.spinner

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.SpinnerAdapter

fun Spinner.onItemSelected(selected: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selected.invoke(position)
        }
    }
}

fun Spinner.setAdapterAndSelection(customAdapter: SpinnerAdapter, index: Int) {
    adapter = customAdapter
    setSelection(index)
}

fun Spinner.setAdapterAndSelection(context: Context, names: List<String>, index: Int) {
    val customAdapter = br.com.suitesistemas.portsmobile.view.adapter.SpinnerAdapter(context, names)
    customAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    adapter = customAdapter
    setSelection(index)
}