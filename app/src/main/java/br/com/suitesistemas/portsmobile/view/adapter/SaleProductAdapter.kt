package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.ProductDetail
import br.com.suitesistemas.portsmobile.view.viewHolder.SaleProductViewHolder

class SaleProductAdapter(private val context: Context,
                         private val products: LinkedHashMap<Product, ProductDetail>
) : RecyclerView.Adapter<SaleProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SaleProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_sale_product, parent, false)
        return SaleProductViewHolder(view)
    }

    override fun onBindViewHolder(holderSelectSale: SaleProductViewHolder, position: Int) {
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
            holderSelectSale.bindView(product, color, detail.quantity)
        }
    }

    override fun getItemCount() = products.size

}