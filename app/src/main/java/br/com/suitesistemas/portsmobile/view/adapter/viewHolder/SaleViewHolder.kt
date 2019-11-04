package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.toStringFormat
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import kotlinx.android.synthetic.main.adapter_sale.view.*

class SaleViewHolder(private val adapterView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(adapterView) {

    val clientName = adapterView.sale_adapter_client_name
    val value = adapterView.sale_adapter_value
    val date = adapterView.sale_adapter_date
    val menu = adapterView.sale_adapter_menu
    private val utils = DoubleUtils()

    fun bindView(sale: Sale) {
        clientName.text = sale.fky_cliente.dsc_nome_pessoa
        value.text = adapterView.context.getString(R.string.preco_adapter, utils.toStringFormat(sale.dbl_total_venda))
        date.text = sale.dat_emissao.toStringFormat()
    }

}