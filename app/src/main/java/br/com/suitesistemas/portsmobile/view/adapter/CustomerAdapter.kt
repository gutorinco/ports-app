package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.view.viewHolder.CustomerViewHolder

class CustomerAdapter(private val context: Context,
                      private val customers: MutableList<Customer>) : Adapter<CustomerViewHolder>(), CustomAdapter<Customer> {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CustomerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_customer_adapter, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customers[position]
        holder.bindView(customer)
    }

    override fun getItemCount() = customers.size

    override fun setAdapter(list: List<Customer>) {
        customers.clear()
        customers.addAll(list)
        notifyDataSetChanged()
    }

}