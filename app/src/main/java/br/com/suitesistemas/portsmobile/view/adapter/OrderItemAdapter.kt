package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.OrderGridItem
import br.com.suitesistemas.portsmobile.model.entity.OrderItem
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.OrderItemViewHolder

class OrderItemAdapter(context: Context,
                       items: MutableList<OrderItem>,
                       private val grids: MutableList<OrderGridItem>,
                       private val delete: (position: Int) -> Unit,
                       private val edit: (position: Int) -> Unit
) : BaseAdapter<OrderItem, OrderItemViewHolder>(context, items) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_order_item, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = list[position]
        val grid = grids.find { it.ids.cod_sequencia == item.cod_sequencia }
        val boxValue = grid?.int_caixa ?: 0

        holder.bindView(item, boxValue)
        holder.menu.setOnClickListener {
            super.createPopup(it, {
                delete(position)
            }, {
                edit(position)
            })
        }
    }

}