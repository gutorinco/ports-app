package br.com.suitesistemas.portsmobile.view.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.entity.Color
import kotlinx.android.synthetic.main.adapter_select_color.view.*

class SelectColorViewHolder(adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val checkbox = adapterView.select_color_adapter_checkbox
    val name = adapterView.select_color_adapter_name

    fun bindView(color: Color) {
        name.text = color.dsc_cor
    }

}