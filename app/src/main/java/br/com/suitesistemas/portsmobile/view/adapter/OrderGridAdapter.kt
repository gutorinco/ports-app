package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.entity.OrderGridItem
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.OrderGridViewHolder

class OrderGridAdapter(context: Context,
                       grids: MutableList<OrderGridItem>,
                       private val combination: Combination,
                       private val delete: (position: Int) -> Unit
) : BaseAdapter<OrderGridItem, OrderGridViewHolder>(context, grids) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): OrderGridViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_order_grid, parent, false)
        return OrderGridViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderGridViewHolder, position: Int) {
        val item = list[position]
        holder.bindView(item, combination)
        holder.menu.setOnClickListener {
            super.createPopup(it) {
                delete(position)
            }
        }
    }

}