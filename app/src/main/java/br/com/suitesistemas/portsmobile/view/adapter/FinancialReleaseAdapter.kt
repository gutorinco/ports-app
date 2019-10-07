package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.FinancialReleaseViewHolder

class FinancialReleaseAdapter(context: Context, releases: MutableList<FinancialRelease>) :
        BaseAdapter<FinancialRelease, FinancialReleaseViewHolder>(context, releases) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): FinancialReleaseViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_financial, parent, false)
        return FinancialReleaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinancialReleaseViewHolder, position: Int) {
        val release = list[position]
        holder.bindView(release)
    }

}