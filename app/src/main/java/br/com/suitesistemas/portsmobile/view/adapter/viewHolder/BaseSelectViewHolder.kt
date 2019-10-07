package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_base_select.view.*

class BaseSelectViewHolder(adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val checkbox = adapterView.base_select_adapter_checkbox
    val name = adapterView.base_select_adapter_name

    fun bindView(name: String) {
        this.name.text = name
    }

}