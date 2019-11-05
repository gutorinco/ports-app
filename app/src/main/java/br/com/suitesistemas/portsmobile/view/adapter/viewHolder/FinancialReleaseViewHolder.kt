package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.core.content.ContextCompat
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.toStringFormat
import br.com.suitesistemas.portsmobile.model.entity.FinancialRelease
import kotlinx.android.synthetic.main.adapter_financial.view.*
import java.util.*

class FinancialReleaseViewHolder(private val adapterView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(adapterView) {

    val name = adapterView.financial_adapter_name
    val reference = adapterView.financial_adapter_reference
    val emissionDate = adapterView.financial_adapter_emission_date
    val paymentDate = adapterView.financial_adapter_payment_date
    val dueDate = adapterView.financial_adapter_due_date
    val statusName = adapterView.financial_adapter_status_name
    val status = adapterView.financial_adapter_status

    fun bindView(release: FinancialRelease) {
        name.text  = release.fky_pessoa.dsc_nome_pessoa
        reference.text = getString(R.string.lanc_adapter_ref, release.dsc_referencia)
        emissionDate.text = getString(R.string.lanc_adapter_emissao, release.dat_emissao.toStringFormat())
        dueDate.text = getString(R.string.lanc_adapter_vencimento, release.dat_vencimento.toStringFormat())
        if (release.dat_pagamento == null) {
            paymentDate.visibility = View.GONE
        } else {
            paymentDate.text = getString(R.string.lanc_adapter_vencimento, release.dat_pagamento!!.toStringFormat())
        }
        when (release.fky_situacao_pagamento.dsc_situacao_pagamento!!.toUpperCase(Locale.ROOT)) {
            getString(R.string.em_aberto) -> setStatus(R.string.em_aberto, R.drawable.filled_circle_primary)
            getString(R.string.cancelado) -> setStatus(R.string.cancelado, R.drawable.filled_circle_accent)
            getString(R.string.pago) -> setStatus(R.string.pago, R.drawable.filled_circle_success)
        }
    }

    private fun getString(stringResource: Int, param: String? = null): String {
        if (param == null)
            return adapterView.context.getString(stringResource)
        return adapterView.context.getString(stringResource, param)
    }

    private fun setStatus(name: Int, drawableId: Int) {
        statusName.text = getString(name)
        status.background = ContextCompat.getDrawable(adapterView.context, drawableId)
    }

}