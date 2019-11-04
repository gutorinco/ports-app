package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.extensions.getPhoneNumber
import br.com.suitesistemas.portsmobile.custom.extensions.numbersOnly
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository
import br.com.suitesistemas.portsmobile.service.customer.CustomerRepository

class CustomerFormViewModel(application: Application) : FormViewModel<Customer>(application) {

    @Bindable var customer = MutableLiveData<Customer>()
    @Bindable var labelInscEstadual = MutableLiveData<String>("Identidade")
    @Bindable var labelCpfCnpj = MutableLiveData<String>("CPF")
    @Bindable var labelName = MutableLiveData<String>("Nome")
    @Bindable var selectedType = MutableLiveData<String>("FÍSICA")
    @Bindable var uf = MutableLiveData<String>("MG")
    val inscs = mutableListOf("ISENTO", "NÃO CONTRIBUINTE")
    val types = mutableListOf("FÍSICA", "JURÍDICA")
    val ufs = br.com.suitesistemas.portsmobile.model.ufs
    private var initializingEdition = false
    private lateinit var customerRepository: CustomerRepository

    init {
        customer.value = Customer()
    }

    fun initRepositories(companyName: String) {
        customerRepository = CustomerRepository(companyName)
        companyRepository  = CompanyRepository(companyName)
    }

    fun concat(customerToConcat: Customer) {
        customer.value = customerToConcat
        customer.value?.dsc_fone_01 = customerToConcat.dsc_ddd_01 + customerToConcat.dsc_fone_01
        customer.value?.dsc_celular_01 = customerToConcat.dsc_ddd_celular_01 + customerToConcat.dsc_celular_01
        if (!customerToConcat.dsc_cpf_cnpj.isNullOrEmpty()) {
            val cpfCnpj = customerToConcat.dsc_cpf_cnpj!!.numbersOnly()
            if (cpfCnpj.length > 11)
                selectedType.value = "JURÍDICA"
            initializingEdition = true
        }
        if (!customerToConcat.dsc_rg_insc_estadual.isNullOrEmpty()) {
            val values = customerToConcat.dsc_rg_insc_estadual!!.split("-")
            if (values.size == 2) {
                uf.value = values[0]
                customer.value?.dsc_rg_insc_estadual = values[1]
                if (values[1] == "ISENTO" || values[1] == "NÃO CONTRIBUINTE")
                    selectedType.value = "JURÍDICA"
            }
        }
    }

    override fun getCompanyIndex(): Int = companies.indexOfFirst { company ->
        company.cod_empresa == customer.value?.fky_empresa?.cod_empresa
    }
    
    fun getTypeIndex(): Int {
        return if (initializingEdition) {
            types.indexOf(selectedType.value)
        } else {
            0
        }
    }

    fun onTypeSelected(position: Int): String {
        selectedType.value = types[position]
        if (initializingEdition) {
            initializingEdition = false
        } else {
            customer.value?.dsc_cpf_cnpj = ""
        }
        return customer.value?.dsc_cpf_cnpj!!
    }

    fun validateForm() {
        val customer = Customer(this.customer.value!!)

        if (companies.isNullOrEmpty())
            throw InvalidValueException(getStringRes(R.string.nenhuma_empresa))

        if (customer.dsc_nome_pessoa.isEmpty()) {
            throw InvalidValueException("Nome", getStringRes(R.string.obrigatorio))
        } else if (customer.flg_situacao_cliente.toString().isEmpty()) {
            throw InvalidValueException(getStringRes(R.string.campo_obrigatorio, "Situação"))
        }

        if (selectedType.value == "JURÍDICA" && customer.dsc_rg_insc_estadual.isNullOrEmpty()) {
            throw InvalidValueException("Insc", getStringRes(R.string.obrigatorio))
        } else if (!customer.dsc_rg_insc_estadual.isNullOrEmpty()) {
            if (uf.value.isNullOrEmpty() || uf.value?.length!! < 2) {
                throw InvalidValueException("UF", getStringRes(R.string.obrigatorio))
            } else {
                if (selectedType.value!! == "FÍSICA") {
                    customer.dsc_rg_insc_estadual = uf.value!! + "-" + customer.dsc_rg_insc_estadual
                } else {
                    val inscEstadual = customer.dsc_rg_insc_estadual
                    if (inscEstadual == "ISENTO" || inscEstadual == "NÃO CONTRIBUINTE") {
                        customer.dsc_rg_insc_estadual = inscEstadual
                    } else {
                        customer.dsc_rg_insc_estadual = uf.value!! + "-" + inscEstadual
                    }
                }
            }
        }

        val phone = customer.dsc_fone_01?.getPhoneNumber() ?: throw InvalidValueException("Telefone", getStringRes(R.string.numero_nao_aceito))
        customer.dsc_ddd_01 = phone.ddd
        customer.dsc_fone_01 = phone.number

        val celPhone = customer.dsc_celular_01?.getPhoneNumber() ?: throw InvalidValueException("Celular", getStringRes(R.string.numero_nao_aceito))
        customer.dsc_ddd_celular_01 = celPhone.ddd
        customer.dsc_celular_01 = celPhone.number

        this.customer.value = Customer(customer)
    }

    fun insert(firebaseToken: String) {
        insertResponse = customerRepository.insert(getJsonRequest("pessoa", customer.value!!, firebaseToken))
    }

    fun update(firebaseToken: String) {
        updateResponse = customerRepository.update(getJsonRequest("pessoa", customer.value!!, firebaseToken))
    }

    fun peopleTypeSelected(type: String) {
        if (type == "JURÍDICA") {
            labelInscEstadual.value = "Insc. Estadual"
            labelCpfCnpj.value = "CNPJ"
            labelName.value = "Razão Social"
        } else {
            labelInscEstadual.value = "Identidade"
            labelCpfCnpj.value = "CPF"
            labelName.value = "Nome"
        }
    }

}