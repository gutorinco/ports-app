package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.Order
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import kotlinx.android.synthetic.main.adapter_order.view.*
import java.text.SimpleDateFormat
import java.util.*

class OrderViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val clientName = adapterView.order_adapter_client_name
    val date = adapterView.order_adapter_date
    val paymentCondition = adapterView.order_adapter_payment_condition
    val totalOrder = adapterView.order_adapter_total_order
    val totalItem = adapterView.order_adapter_total_item
    val menu = adapterView.order_adapter_menu
    private val utils = DoubleUtils()

    fun bindView(order: Order) {
        clientName.text = order.fky_cliente.dsc_nome_pessoa
        paymentCondition.text = adapterView.context.getString(R.string.entre_parenteses, order.fky_condicao_pagamento.dsc_condicao_pagamento)
        totalItem.text = adapterView.context.getString(R.string.adapter_total_item, utils.toStringFormat(order.dbl_total_item))
        totalOrder.text = adapterView.context.getString(R.string.adapter_total_pedido, utils.toStringFormat(order.dbl_total_pedido))

        val textDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(order.dat_emissao)
        date.text = textDate
    }

}