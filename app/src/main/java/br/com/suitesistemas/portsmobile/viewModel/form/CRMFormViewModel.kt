package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.entity.CRM
import br.com.suitesistemas.portsmobile.service.crm.CRMRepository
import java.util.*

class CRMFormViewModel(application: Application) : FormViewModel<CRM>(application) {

    @Bindable var crm = MutableLiveData<CRM>()
    @Bindable var showList = MutableLiveData<Boolean>()
    @Bindable var label = MutableLiveData<String>()
    private lateinit var repository: CRMRepository

    init {
        crm.value = CRM()
        showList.value = false
        label.value = ""
    }

    fun initRepository(companyName: String) {
        repository = CRMRepository(companyName)
    }

    fun concat(crm: CRM) {
        this.crm.value = crm
    }

    fun validateForm() {
        with (CRM(crm.value!!)) {
            if (fky_pedido == null && fky_venda == null)
                throw InvalidValueException(getStringRes(R.string.selecione_pedido_ou_venda))
            if (flg_tipo_crm.isNullOrEmpty())
                throw InvalidValueException(getStringRes(R.string.selecione_tipo))
            if (int_status < 1 || int_status > 3)
                throw InvalidValueException(getStringRes(R.string.selecione_status))
            if (fky_cliente.dsc_nome_pessoa.isEmpty())
                throw InvalidValueException("Cliente", getStringRes(R.string.obrigatorio))
            if (dsc_crm.isEmpty())
                throw InvalidValueException("Descricao", getStringRes(R.string.obrigatorio))

            if (num_codigo_online.isEmpty())
                dat_cadastro = Date()

            crm.value = CRM(this)
        }
    }

    fun checkListVisibility() {
        showList.value = crm.value!!.fky_pedido != null || crm.value!!.fky_venda != null
    }

    fun insert(firebaseToken: String) {
        insertResponse = repository.insert(getJsonRequest("crm", crm.value!!, firebaseToken))
    }

    fun update(firebaseToken: String) {
        updateResponse = repository.update(getJsonRequest("crm", crm.value!!, firebaseToken))
    }

}