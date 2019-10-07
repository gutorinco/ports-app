package br.com.suitesistemas.portsmobile.view.viewHolder

import android.view.View
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Sale
import kotlinx.android.synthetic.main.adapter_sale.view.*
import java.text.SimpleDateFormat
import java.util.*

class SaleViewHolder(private val adapterView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(adapterView) {

    val clientName = adapterView.sale_adapter_client_name
    val value = adapterView.sale_adapter_value
    val date = adapterView.sale_adapter_date
    val menu = adapterView.sale_adapter_menu

    fun bindView(sale: Sale) {
        clientName.text = sale.fky_cliente.dsc_nome_pessoa
        value.text = adapterView.context.getString(R.string.preco_adapter, sale.dbl_total_venda.toString())

        val textDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(sale.dat_emissao)
        date.text = textDate
    }

}