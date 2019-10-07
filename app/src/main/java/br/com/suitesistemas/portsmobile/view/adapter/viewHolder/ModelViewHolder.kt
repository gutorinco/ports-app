package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Model
import kotlinx.android.synthetic.main.adapter_model.view.*

class ModelViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val menu = adapterView.model_adapter_menu
    val name = adapterView.model_adapter_name
    val priceValue = adapterView.model_adapter_price
    val priceFinancedValue = adapterView.model_adapter_price_financed

    fun bindView(model: Model) {
        name.text = model.dsc_modelo
        priceValue.text = getString(R.string.produto_vista_adapter, model.dbl_preco_unit_vista.toString())
        priceFinancedValue.text = getString(R.string.produto_prazo_adapter, model.dbl_preco_unit_prazo.toString())
    }

    private fun getString(stringResource: Int, param: Any): String {
        return adapterView.context.getString(stringResource, param)
    }

}