package br.com.suitesistemas.portsmobile.view.viewHolder

import android.view.View
import android.widget.ArrayAdapter
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import kotlinx.android.synthetic.main.adapter_product_quantity.view.*

class ProductQuantityViewHolder(private val adapterView: View) {

    val quantity = adapterView.product_qnt_dialog_quantity
    val color = adapterView.product_qnt_dialog_color
    val name = adapterView.product_qnt_dialog_name
    val saleValue = adapterView.product_qnt_dialog_sale
    val saleFinancedValue = adapterView.product_qnt_dialog_sale_financed

    fun bindView(product: Product, colorsNameAdapter: ArrayAdapter<String?>) {
        name.text = product.dsc_produto
        saleValue.text = getString(R.string.a_vista_adapter, product.dbl_venda_vista.toString())
        saleFinancedValue.text = getString(R.string.a_prazo_adapter, product.dbl_venda_prazo.toString())
        quantity.setText(1.toString())

        colorsNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        color.adapter = colorsNameAdapter
        color.setSelection(0)
    }

    private fun getString(stringResource: Int, param: String): String {
        return adapterView.context.getString(stringResource, param)
    }

}