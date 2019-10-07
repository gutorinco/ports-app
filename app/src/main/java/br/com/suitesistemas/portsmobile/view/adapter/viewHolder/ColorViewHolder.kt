package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.entity.Color
import kotlinx.android.synthetic.main.adapter_color.view.*

class ColorViewHolder(adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val name = adapterView.color_adapter_name
    val menu = adapterView.color_adapter_menu

    fun bindView(color: Color) {
        name.text  = color.dsc_cor
    }

}