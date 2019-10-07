package br.com.suitesistemas.portsmobile.view.viewHolder

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.suitesistemas.portsmobile.entity.Customer
import kotlinx.android.synthetic.main.adapter_customer.view.*

class CustomerViewHolder(adapterView: View) : ViewHolder(adapterView) {

    val name = adapterView.customer_adapter_name
    val email = adapterView.customer_adapter_email
    val menu = adapterView.customer_adapter_menu

    fun bindView(customer: Customer) {
        name.text  = customer.dsc_nome_pessoa
        email.text = customer.dsc_email
        if (customer.dsc_email.isNullOrEmpty()) {
            val params = name.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = 15
            params.topMargin = 15
            name.layoutParams = params
            email.visibility = View.GONE
        } else {
            val params = name.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = 0
            params.topMargin = 0
            name.layoutParams = params
            email.visibility = View.VISIBLE
        }
    }

}