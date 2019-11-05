package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.Product
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import kotlinx.android.synthetic.main.adapter_select_product.view.*

class SelectProductViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val checkbox = adapterView.select_product_adapter_checkbox
    val name = adapterView.select_product_adapter_name
    val saleValue = adapterView.select_product_adapter_sale
    val saleFinancedValue = adapterView.select_product_adapter_sale_financed
    private val utils = DoubleUtils()

    fun bindView(product: Product) {
        name.text = product.dsc_produto
        saleValue.text = getString(R.string.a_vista_adapter, utils.toStringFormat(product.dbl_venda_vista ?: 0.0))
        saleFinancedValue.text = getString(R.string.a_prazo_adapter, utils.toStringFormat(product.dbl_venda_prazo ?: 0.0))
    }

    private fun getString(stringResource: Int, param: String): String {
        return adapterView.context.getString(stringResource, param)
    }

}