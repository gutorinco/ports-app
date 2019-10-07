package br.com.suitesistemas.portsmobile.view.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import kotlinx.android.synthetic.main.adapter_product.view.*

class ProductViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val menu = adapterView.product_adapter_menu
    val name = adapterView.product_adapter_name
    val saleValue = adapterView.product_adapter_sale
    val saleFinancedValue = adapterView.product_adapter_sale_financed

    fun bindView(product: Product) {
        name.text = product.dsc_produto
        saleValue.text = getString(R.string.produto_vista_adapter, product.dbl_venda_vista.toString())
        saleFinancedValue.text = getString(R.string.produto_prazo_adapter, product.dbl_venda_prazo.toString())
    }

    private fun getString(stringResource: Int, param: Any): String {
        return adapterView.context.getString(stringResource, param)
    }

}