package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.Combination
import br.com.suitesistemas.portsmobile.model.entity.OrderGridItem
import kotlinx.android.synthetic.main.adapter_order_grid.view.*

class OrderGridViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val menu = adapterView.order_grid_adapter_menu
    val number = adapterView.order_grid_adapter_number
    val combination = adapterView.order_grid_adapter_combination
    val quantity = adapterView.order_grid_adapter_quantity

    fun bindView(grid: OrderGridItem, combination: Combination) {
        number.text = adapterView.context.getString(R.string.numero_label, grid.dsc_numero)
        this.combination.text = adapterView.context.getString(R.string.entre_parenteses, combination.dsc_combinacao)
        quantity.text = adapterView.context.getString(R.string.quantidade_label, grid.dbl_quantidade.toInt().toString())
    }

}