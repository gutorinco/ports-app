package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.model.entity.Combination
import kotlinx.android.synthetic.main.adapter_combination.view.*

class CombinationViewHolder(adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val menu = adapterView.combination_adapter_menu
    val name = adapterView.combination_adapter_name

    fun bindView(combination: Combination) {
        name.text  = combination.dsc_combinacao
    }

}