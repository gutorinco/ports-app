package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.BaseSelectViewHolder

class SelectCombinationAdapter(context: Context,
                               combinations: MutableList<Combination>,
                               private val onChecked: (checked: Boolean, position: Int) -> Unit
) : BaseAdapter<Combination, BaseSelectViewHolder>(context, combinations) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BaseSelectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_base_select, parent, false)
        return BaseSelectViewHolder(view)
    }

    override fun onBindViewHolder(holderBaseSelect: BaseSelectViewHolder, position: Int) {
        val combination = list[position]
        holderBaseSelect.bindView(combination.dsc_combinacao)
        holderBaseSelect.checkbox.setOnClickListener {
            it?.let { view ->
                val checkbox = view as CheckBox
                onChecked(checkbox.isChecked, holderBaseSelect.adapterPosition)
            }
        }
    }

}