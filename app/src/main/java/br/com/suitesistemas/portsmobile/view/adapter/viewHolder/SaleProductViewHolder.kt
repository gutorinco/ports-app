package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import kotlinx.android.synthetic.main.adapter_sale_product.view.*

class SaleProductViewHolder(private val adapterView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(adapterView) {

    val menu = adapterView.sale_product_adapter_menu
    val name = adapterView.sale_product_adapter_name
    val colorName = adapterView.sale_product_adapter_color_name
    val quantity = adapterView.sale_product_adapter_quantity
    val saleValue = adapterView.sale_product_adapter_sale
    val saleFinancedValue = adapterView.sale_product_adapter_sale_financed
    private val utils = DoubleUtils()

    fun bindView(product: Product, color: Color, quantityValue: Int) {
        name.text = product.dsc_produto
        if (!color.dsc_cor.isNullOrEmpty())
            colorName.text = getString(R.string.em_parenteses, color.dsc_cor!!)
        quantity.text = getString(R.string.quantidade_adapter, quantityValue)
        saleValue.text = getString(R.string.a_vista_adapter, utils.toStringFormat(product.dbl_venda_vista ?: 0.0))
        saleFinancedValue.text = getString(R.string.a_prazo_adapter, utils.toStringFormat(product.dbl_venda_prazo ?: 0.0))
    }

    private fun getString(stringResource: Int, param: Any): String {
        return adapterView.context.getString(stringResource, param)
    }

}