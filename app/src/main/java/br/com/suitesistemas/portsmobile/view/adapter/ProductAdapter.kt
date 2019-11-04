package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.enums.ESystemType
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.ProductViewHolder

class ProductAdapter(context: Context,
                     products: MutableList<Product>,
                     private val systemType: ESystemType,
                     private val delete: (position: Int) -> Unit,
                     private val edit: (position: Int) -> Unit
) : BaseAdapter<Product, ProductViewHolder>(context, products) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = list[position]
        holder.bindView(product, systemType)
        holder.menu.setOnClickListener {
            super.createPopup(it, {
                delete(position)
            }, {
                edit(position)
            })
        }
    }

}