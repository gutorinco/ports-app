package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.CombinationViewHolder

class CombinationAdapter(context: Context,
                         combinations: MutableList<Combination>
) : BaseAdapter<Combination, CombinationViewHolder>(context, combinations) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CombinationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_combination, parent, false)
        return CombinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: CombinationViewHolder, position: Int) {
        holder.bindView(list[position])
    }

}