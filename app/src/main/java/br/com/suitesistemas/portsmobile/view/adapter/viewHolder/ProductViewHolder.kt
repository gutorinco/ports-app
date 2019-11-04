package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.enums.ESystemType
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import kotlinx.android.synthetic.main.adapter_product.view.*

class ProductViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val menu = adapterView.product_adapter_menu
    val name = adapterView.product_adapter_name
    val reference = adapterView.product_adapter_reference
    val code = adapterView.product_adapter_code
    val saleValue = adapterView.product_adapter_sale
    val saleFinancedValue = adapterView.product_adapter_sale_financed
    private val utils = DoubleUtils()

    fun bindView(product: Product, systemType: ESystemType) {
        name.text = product.dsc_produto
        if (product.dsc_referencia.isNullOrEmpty()) {
            reference.visibility = View.GONE
        } else {
            reference.text = getString(R.string.adapter_referencia, product.dsc_referencia!!)
        }

        val codeValue: Int = when {
            systemType == ESystemType.O -> product.cod_online
            product.cod_produto != null -> product.cod_produto!!
            else -> 0
        }

        if (codeValue > 0) {
            code.text = getString(R.string.adapter_codigo, codeValue)
        } else {
            code.visibility = View.GONE
        }

        saleValue.text = getString(R.string.a_vista_adapter, utils.toStringFormat(product.dbl_venda_vista ?: 0.0))
        saleFinancedValue.text = getString(R.string.a_prazo_adapter, utils.toStringFormat(product.dbl_venda_prazo ?: 0.0))
    }

    private fun getString(stringResource: Int, param: Any): String {
        return adapterView.context.getString(stringResource, param)
    }

}