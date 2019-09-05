package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.model.CustomerSituation
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository
import br.com.suitesistemas.portsmobile.service.customer.CustomerRepository

class CustomerFormViewModel(application: Application) : FormViewModel<Customer>(application) {

    @Bindable
    var customer = MutableLiveData<Customer>()
    private lateinit var customerRepository: CustomerRepository
    private lateinit var companyRepository: CompanyRepository

    init {
        customer.value = Customer()
    }

    fun initRepositories(companyName: String) {
        customerRepository = CustomerRepository(companyName)
        companyRepository  = CompanyRepository(companyName)
    }

    fun fetchAllCompanies() {
        companiesResponse = when (companies.isNullOrEmpty()) {
            true -> companyRepository.findAll()
            false -> getApiResponseFromExistList(companies)
        }
    }

    fun concat(customerToConcat: Customer) {
        customer.value = customerToConcat
        customer.value?.dsc_fone_01 = customerToConcat.dsc_ddd_01 + customerToConcat.dsc_fone_01
        customer.value?.dsc_celular_01 = customerToConcat.dsc_ddd_celular_01 + customerToConcat.dsc_celular_01
    }

    fun validateForm(situation: String, companyPosition: Int) {
        val customer = Customer()
        customer.copy(this.customer.value!!)

        if (companies.isNullOrEmpty())
            throw InvalidValueException(getStringRes(R.string.nenhuma_empresa))

        customer.fky_empresa = companies[companyPosition]
        customer.flg_situacao_cliente = CustomerSituation(situation).flag

        if (customer.dsc_nome_pessoa.isEmpty()) {
            throw InvalidValueException("Nome", getStringRes(R.string.obrigatorio))
        } else if (customer.flg_situacao_cliente.toString().isEmpty()) {
            throw InvalidValueException(getStringRes(R.string.campo_obrigatorio, "Situação"))
        }

        var phone = customer.dsc_fone_01!!
        phone = phone.replace("null", "")
        if (phone.contains("-") && phone.length == 14) {
            customer.dsc_ddd_01 = phone.substring(1, 3)
            customer.dsc_fone_01 = phone.substring(5, 14).replace("-", "")
        } else if (phone.length == 10) {
            customer.dsc_ddd_01 = phone.substring(0, 2)
            customer.dsc_fone_01 = phone.substring(2, 10)
        } else if (phone.isNotEmpty())  {
            throw InvalidValueException("Telefone", getStringRes(R.string.numero_nao_aceito))
        }

        var cellPhone = customer.dsc_celular_01!!
        cellPhone = cellPhone.replace("null", "")
        if (cellPhone.contains("-") && cellPhone.length == 15) {
            customer.dsc_ddd_celular_01 = cellPhone.substring(1, 3)
            customer.dsc_celular_01 = cellPhone.substring(5, 15).replace("-", "")
        } else if (cellPhone.length == 11) {
            customer.dsc_ddd_celular_01 = cellPhone.substring(0, 2)
            customer.dsc_celular_01 = cellPhone.substring(2, 11)
        } else if (cellPhone.isNotEmpty()) {
            throw InvalidValueException("Celular", getStringRes(R.string.numero_nao_aceito))
        }

        this.customer.value?.copy(customer)
    }

    fun save() {
        if (customer.value?.num_codigo_online.isNullOrEmpty())
             insert()
        else update()
    }

    private fun insert() {
        insertResponse = customerRepository.insert(getJsonRequest("pessoa", customer.value!!))
    }

    private fun update() {
        updateResponse = customerRepository.update(getJsonRequest("pessoa", customer.value!!))
    }

}