package br.com.suitesistemas.portsmobile.view.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_spinner_array.view.*

class SpinnerViewHolder(adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val spinnerItem = adapterView.spinner_adapter_name

    fun bindView(name: String) {
        spinnerItem.text = name
    }

}