package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.utils.PopupMenuUtils
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.CombinationViewHolder

class CombinationAdapter(context: Context,
                         combinations: MutableList<Combination>,
                         private val delete: ((Int) -> Unit)? = null
) : BaseAdapter<Combination, CombinationViewHolder>(context, combinations) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CombinationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_combination, parent, false)
        return CombinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: CombinationViewHolder, position: Int) {
        holder.bindView(list[position])
        if (delete != null) {
            holder.menu.setOnClickListener {
                PopupMenuUtils.createPopup(it, context) {
                    delete!!(position)
                }
            }
        } else {
            holder.menu.visibility = View.INVISIBLE
        }
    }

}