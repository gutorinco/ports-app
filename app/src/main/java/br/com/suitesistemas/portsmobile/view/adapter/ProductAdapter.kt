package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.ProductDetail
import br.com.suitesistemas.portsmobile.view.viewHolder.ProductViewHolder

class ProductAdapter(private val context: Context,
                     private val products: LinkedHashMap<Product, ProductDetail>) : androidx.recyclerview.widget.RecyclerView.Adapter<ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_product_adapter, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holderSelect: ProductViewHolder, position: Int) {
        val product = ArrayList(products.keys)[position]
        products[product]?.let { detail ->
            var color = Color()
            if (detail.colors.isNotEmpty()) {
                val productColor = detail.colors.find {
                        productColor -> productColor.cod_produto.cod_produto == product.cod_produto
                }
                productColor?.let {
                    color = it.cod_cor
                }
            }
            holderSelect.bindView(product, color, detail.quantity)
        }
    }

    override fun getItemCount() = products.size

}