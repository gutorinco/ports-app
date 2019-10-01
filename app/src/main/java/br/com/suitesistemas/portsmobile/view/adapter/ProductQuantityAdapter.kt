package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.EditText
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.spinner.onItemSelected
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.entity.ProductColor
import br.com.suitesistemas.portsmobile.model.ProductDetail
import br.com.suitesistemas.portsmobile.view.viewHolder.ProductQuantityViewHolder

class ProductQuantityAdapter(private val context: Context,
                             private val products: LinkedHashMap<Product, ProductDetail> = linkedMapOf(),
                             private val onChange: (quantity: Int, color: ProductColor, product: Product) -> Unit
) : BaseAdapter() {

    private val editText: MutableList<EditText> = mutableListOf()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ProductQuantityViewHolder
        val product = getItem(position)
        val productColors = products[product]?.colors

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_product_quantity_adapter, parent, false)
            holder = ProductQuantityViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ProductQuantityViewHolder
        }

        val colors = productColors!!.map { it.cod_cor }
        val colorsName = colors.map { it.dsc_cor }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, colorsName)

        editText.add(holder.quantity)

        holder.bindView(product, adapter)
        holder.quantity.setOnFocusChangeListener { _, hasFocus ->
            val text = holder.quantity.text.toString()
            if (!text.isEmpty() && !hasFocus) {
                val colorSelected = productColors.find { it.cod_cor.dsc_cor == holder.color.selectedItem }
                colorSelected?.let {
                    onChange(text.toInt(), it, product)
                }
            }
        }
        holder.color.onItemSelected {
            var quantity = 0
            val quantityText = holder.quantity.text.toString()
            if (!quantityText.isEmpty())
                quantity = quantityText.toInt()
            onChange(quantity, productColors[it], product)
        }

        return view
    }

    override fun getItem(position: Int) = ArrayList(products.keys)[position]

    override fun getItemId(position: Int) = 0.toLong()

    override fun getCount() = products.size

    fun clearFocus() {
        editText.forEach { it.clearFocus() }
    }

}