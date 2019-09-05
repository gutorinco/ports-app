package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.view.viewHolder.FinancialReleaseViewHolder

class FinancialReleaseAdapter(private val context: Context,
                              private val releases: MutableList<FinancialRelease>
) : androidx.recyclerview.widget.RecyclerView.Adapter<FinancialReleaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): FinancialReleaseViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_financial_adapter, parent, false)
        return FinancialReleaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinancialReleaseViewHolder, position: Int) {
        val release = releases[position]
        holder.bindView(release)
    }

    override fun getItemCount() = releases.size

    fun setAdapter(releases: MutableList<FinancialRelease>) {
        this.releases.clear()
        this.releases.addAll(releases)
        notifyDataSetChanged()
    }
}