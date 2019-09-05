package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.view.viewHolder.SelectProductViewHolder

class SelectProductAdapter(private val context: Context,
                           private val products: MutableList<Product>,
                           private val onChecked: (checked: Boolean, position: Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<SelectProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SelectProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_select_product_adapter, parent, false)
        return SelectProductViewHolder(view)
    }

    override fun onBindViewHolder(holderSelect: SelectProductViewHolder, position: Int) {
        val product = products[position]
        holderSelect.bindView(product)
        holderSelect.checkbox.setOnClickListener {
            it?.let { view ->
                val checkbox = view as CheckBox
                onChecked(checkbox.isChecked, holderSelect.adapterPosition)
            }
        }
    }

    override fun getItemCount() = products.size

    fun setAdapter(toAdd: List<Product>) {
        products.clear()
        products.addAll(toAdd)
        notifyDataSetChanged()
    }

}