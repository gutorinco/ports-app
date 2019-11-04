package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import android.widget.ArrayAdapter
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import kotlinx.android.synthetic.main.adapter_product_quantity.view.*

class ProductQuantityViewHolder(private val adapterView: View) {

    val quantity = adapterView.product_qnt_dialog_quantity
    val color = adapterView.product_qnt_dialog_color
    val name = adapterView.product_qnt_dialog_name
    val saleValue = adapterView.product_qnt_dialog_sale
    val saleFinancedValue = adapterView.product_qnt_dialog_sale_financed
    private val utils = DoubleUtils()

    fun bindView(product: Product, colorsNameAdapter: ArrayAdapter<String?>) {
        name.text = product.dsc_produto
        saleValue.text = getString(R.string.a_vista_adapter, utils.toStringFormat(product.dbl_venda_vista ?: 0.0))
        saleFinancedValue.text = getString(R.string.a_prazo_adapter, utils.toStringFormat(product.dbl_venda_prazo ?: 0.0))
        quantity.setText(1.toString())

        colorsNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        color.adapter = colorsNameAdapter
        color.setSelection(0)
    }

    private fun getString(stringResource: Int, param: String): String {
        return adapterView.context.getString(stringResource, param)
    }

}