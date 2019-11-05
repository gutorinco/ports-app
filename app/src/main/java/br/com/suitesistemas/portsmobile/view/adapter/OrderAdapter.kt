package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.Order
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.OrderViewHolder

class OrderAdapter(context: Context,
                   orders: MutableList<Order>,
                   private val delete: ((Int) -> Unit)? = null,
                   private val edit: ((Int) -> Unit)? = null
) : BaseAdapter<Order, OrderViewHolder>(context, orders) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = list[position]
        holder.bindView(order)
        if (delete != null && edit != null) {
            holder.menu.setOnClickListener {
                super.createPopup(it, {
                    delete.invoke(position)
                }, {
                    edit.invoke(position)
                })
            }
        } else {
            holder.menu.visibility = View.INVISIBLE
        }
    }

}