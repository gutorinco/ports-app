package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.toStringFormat
import br.com.suitesistemas.portsmobile.entity.CRM
import kotlinx.android.synthetic.main.adapter_crm.view.*

class CRMViewHolder(private val adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val customer = adapterView.crm_adapter_customer
    val description = adapterView.crm_adapter_description
    val occurence = adapterView.crm_adapter_occurence_date
    val solution = adapterView.crm_adapter_solution_date
    val emission = adapterView.crm_adapter_emission_date
    val status = adapterView.crm_adapter_status
    val statusName = adapterView.crm_adapter_status_name
    val menu = adapterView.crm_adapter_menu

    fun bindView(crm: CRM) {
        customer.text  = crm.fky_cliente.dsc_nome_pessoa
        description.text = crm.dsc_crm
        occurence.text = getString(R.string.crm_adapter_ocorrencia, crm.dat_ocorrencia.toStringFormat())
        emission.text = getString(R.string.adapter_data_cadastro, crm.dat_cadastro.toStringFormat())
        if (crm.dat_solucao == null) {
            solution.visibility = View.GONE
        } else {
            solution.visibility = View.VISIBLE
            solution.text = getString(R.string.crm_adapter_solucao, crm.dat_solucao!!.toStringFormat())
        }
        when (crm.int_status) {
            1 -> setStatus(R.string.em_aberto, R.drawable.filled_circle_primary)
            2 -> setStatus(R.string.concluido, R.drawable.filled_circle_success)
            3 -> setStatus(R.string.cancelado, R.drawable.filled_circle_accent)
            else -> setStatus(R.string.em_aberto, R.drawable.filled_circle_primary)
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