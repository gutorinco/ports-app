package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.SaleViewHolder

class SaleAdapter(context: Context,
                  sales: MutableList<Sale>,
                  private val delete: (position: Int) -> Unit,
                  private val edit: (position: Int) -> Unit) :
    BaseAdapter<Sale, SaleViewHolder>(context, sales) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SaleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = list[position]
        holder.bindView(sale)
        holder.menu.setOnClickListener {
            super.createPopup(it, {
                delete(position)
            }, {
                edit(position)
            })
        }
    }

}