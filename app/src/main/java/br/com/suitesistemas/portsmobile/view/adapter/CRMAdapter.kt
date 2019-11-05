package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.CRM
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.CRMViewHolder

class CRMAdapter(context: Context,
            crms: MutableList<CRM>,
            private val delete: (position: Int) -> Unit,
            private val edit: (position: Int) -> Unit) :
            BaseAdapter<CRM, CRMViewHolder>(context, crms) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CRMViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_crm, parent, false)
        return CRMViewHolder(view)
    }

    override fun onBindViewHolder(holder: CRMViewHolder, position: Int) {
        val crm = list[position]
        holder.bindView(crm)
        holder.menu.setOnClickListener {
            super.createPopup(it, {
                delete(position)
            }, {
                edit(position)
            })
        }
    }

}