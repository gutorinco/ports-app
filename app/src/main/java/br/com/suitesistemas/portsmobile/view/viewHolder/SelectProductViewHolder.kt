package br.com.suitesistemas.portsmobile.view.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import kotlinx.android.synthetic.main.adapter_select_product.view.*

class SelectProductViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val checkbox = adapterView.select_product_adapter_checkbox
    val name = adapterView.select_product_adapter_name
    val saleValue = adapterView.select_product_adapter_sale
    val saleFinancedValue = adapterView.select_product_adapter_sale_financed

    fun bindView(product: Product) {
        name.text = product.dsc_produto
        saleValue.text = getString(R.string.produto_vista_adapter, product.dbl_venda_vista.toString())
        saleFinancedValue.text = getString(R.string.produto_prazo_adapter, product.dbl_venda_prazo.toString())
    }

    private fun getString(stringResource: Int, param: String): String {
        return adapterView.context.getString(stringResource, param)
    }

}