package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.view.viewHolder.SaleViewHolder

class SaleAdapter(private val context: Context,
                  private val sales: MutableList<Sale>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SaleViewHolder>(),
    CustomAdapter<Sale> {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SaleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_sale_adapter, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = sales[position]
        holder.bindView(sale)
    }

    override fun getItemCount() = sales.size

    override fun setAdapter(list: List<Sale>) {
        sales.clear()
        sales.addAll(list)
        notifyDataSetChanged()
    }

}