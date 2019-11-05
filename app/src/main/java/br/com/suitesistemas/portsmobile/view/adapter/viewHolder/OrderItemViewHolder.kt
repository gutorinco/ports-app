package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.OrderItem
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import kotlinx.android.synthetic.main.adapter_order_item.view.*

class OrderItemViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val menu = adapterView.order_item_adapter_menu
    val model = adapterView.order_item_adapter_model
    val combination = adapterView.order_item_adapter_combination
    val quantity = adapterView.order_item_adapter_quantity
    val unitPrice = adapterView.order_item_adapter_unit_price
    val discount = adapterView.order_item_adapter_discount
    val subtotal = adapterView.order_item_adapter_subtotal
    val box = adapterView.order_item_adapter_box
    private val utils = DoubleUtils()

    fun bindView(item: OrderItem, boxValue: Int) {
        with (item) {
            model.text = fky_modelo.dsc_modelo
            combination.text = adapterView.context.getString(
                R.string.entre_parenteses,
                fky_combinacao.dsc_combinacao
            )
            quantity.text = adapterView.context.getString(
                R.string.quantidade_label,
                dbl_quantidade.toString()
            )
            unitPrice.text = adapterView.context.getString(
                R.string.preco_unit_label,
                utils.toStringFormat(dbl_preco_unit)
            )
            discount.text = adapterView.context.getString(
                R.string.desconto_label,
                utils.toStringFormat(dbl_desconto ?: 0.0)
            )
            subtotal.text = adapterView.context.getString(
                R.string.subtotal_label,
                utils.toStringFormat(dbl_total_item)
            )
            if (boxValue > 0) {
                box.text = adapterView.context.getString(
                R.string.caixa_label,
                boxValue
            )
            }
        }
    }

}
