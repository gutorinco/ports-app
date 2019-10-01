package br.com.suitesistemas.portsmobile.view.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.entity.Color
import kotlinx.android.synthetic.main.fragment_color_adapter.view.*

class ColorViewHolder(adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val name = adapterView.color_adapter_name
    val menu = adapterView.color_adapter_menu

    fun bindView(color: Color) {
        name.text  = color.dsc_cor
    }

}