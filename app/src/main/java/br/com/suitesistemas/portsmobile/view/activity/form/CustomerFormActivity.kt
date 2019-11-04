package br.com.suitesistemas.portsmobile.view.activity.form

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.databinding.ActivityCustomerFormBinding
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.model.enums.ECustomerSituation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.AutoCompleteAdapter
import br.com.suitesistemas.portsmobile.view.adapter.SpinnerAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.CustomerFormViewModel
import kotlinx.android.synthetic.main.activity_customer_form.*

class CustomerFormActivity : FormActivity<Customer>() {

    private lateinit var viewModel: CustomerFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_form)

        configureActionBar(R.string.cadastro_cliente)
        initViewModel()
        configureDataBinding()
    }

    override fun getBtnConfirm() = customer_form_btn_confirm
    override fun getProgressBar() = customer_form_progressbar
    override fun getLayout() = customer_form

    override fun onResume() {
        super.onResume()
        configureObservers()
        configureForm()
        configureBtnConfirm()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(CustomerFormViewModel::class.java)
        viewModel.initRepositories(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityCustomerFormBinding>(this, R.layout.activity_customer_form)
            .apply {
                lifecycleOwner = this@CustomerFormActivity
                viewModel = this@CustomerFormActivity.viewModel
            }
    }

    private fun configureObservers() {
        with (viewModel) {
            selectedType.observe(this@CustomerFormActivity, Observer<String> {
                it?.let { type -> peopleTypeSelected(type) }
            })

            fetchAllCompanies()
            companiesResponse.observe(this@CustomerFormActivity, observerHandler({
                addAllCompanies(it)
                val companiesName = getCompaniesNames()
                val index = getCompanyIndex()
                customer_form_company.setAdapterAndSelection(this@CustomerFormActivity, companiesName, index)
            }, {
                showMessage(it, R.string.nao_encontrou_empresas)
            }, {
                hideProgress()
            }))
        }
    }

    private fun configureForm() {
        val customerToEdit = intent.getParcelableExtra<Customer>("customer")
        if (customerToEdit != null)
            viewModel.concat(customerToEdit as Customer)
        configureTypeSpinner()
        configureSituationsSpinner()
        configureUFField()
        configureStateInscs()
        customer_form_cell_phone.addPhoneMask()
        customer_form_phone.addPhoneMask()
        customer_form_company.onItemSelected { viewModel.customer.value!!.fky_empresa = viewModel.companies[it] }
    }

    private fun configureTypeSpinner() {
        with(customer_form_type) {
            adapter = SpinnerAdapter(this@CustomerFormActivity, viewModel.types)
            setSelection(viewModel.getTypeIndex())
            onItemSelected {
                configureStateInscs()
                customer_form_cpf_cnpj.setText(viewModel.onTypeSelected(it))
                when (viewModel.selectedType.value) {
                    "JURÍDICA" -> customer_form_cpf_cnpj.addCnpjMask()
                    else -> customer_form_cpf_cnpj.addCpfMask()
                }
            }
        }
    }

    private fun configureSituationsSpinner() {
        val situations = resources.getStringArray(R.array.customerSituations)
        val customerSituation = viewModel.customer.value?.flg_situacao_cliente
        customer_form_situation
            .setAdapterAndSelection(this, situations.toList(), when (customerSituation) {
                ECustomerSituation.N -> 0
                ECustomerSituation.A -> 1
                ECustomerSituation.P -> 2
                ECustomerSituation.C -> 3
                else -> 1
            })
        customer_form_situation.onItemSelected {
            viewModel.customer.value?.flg_situacao_cliente = when (it) {
                0 -> ECustomerSituation.N
                1 -> ECustomerSituation.A
                2 -> ECustomerSituation.P
                3 -> ECustomerSituation.C
                else -> ECustomerSituation.A
            }
        }
    }

    private fun configureUFField() {
        val adapter = AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, viewModel.ufs)
        customer_form_state.setAdapter(adapter)
    }

    private fun configureStateInscs() {
        if (viewModel.selectedType.value == "JURÍDICA") {
            val adapter = AutoCompleteAdapter(this,
                android.R.layout.simple_dropdown_item_1line,
                viewModel.inscs)
            customer_form_insc_estadual.setAdapter(adapter)
        } else {
            customer_form_insc_estadual.setAdapter(null)
        }
    }

    private fun configureBtnConfirm() {
        customer_form_btn_confirm.setOnClickListener {
            executeAfterLoaded {
                hideKeyboard()
                customer_form_name.error = null
                customer_form_phone.error = null
                customer_form_cell_phone.error = null

                try {
                    viewModel.validateForm()
                    showProgress()

                    val firebaseToken = FirebaseUtils.getToken(this)

                    if (viewModel.customer.value?.num_codigo_online.isNullOrEmpty())
                         insert(firebaseToken)
                    else update(firebaseToken)
                } catch (ex: InvalidValueException) {
                    when (ex.field) {
                        "Nome" -> customer_form_name.error = ex.message
                        "Telefone" -> customer_form_phone.error = ex.message
                        "Celular" -> customer_form_cell_phone.error = ex.message
                        "Insc" -> customer_form_insc_estadual.error = ex.message
                        "UF" -> customer_form_state.error = ex.message
                        else -> showMessage(ex.message!!)
                    }
                }
            }
        }
    }

    private fun insert(firebaseToken: String) {
        with (viewModel) {
            insert(firebaseToken)
            insertResponse.observe(this@CustomerFormActivity, observerHandler({
                customer.value = it
                created("customer_response", it)
            }, {
                showMessage(it, R.string.falha_inserir_cliente)
            }, {
                hideProgress()
            }))
        }
    }

    private fun update(firebaseToken: String) {
        with (viewModel) {
            update(firebaseToken)
            updateResponse.observe(this@CustomerFormActivity, observerHandler({
                customer.value?.version = it.version
                created("customer_response", customer.value!!)
            }, {
                showMessage(it, R.string.falha_atualizar_cliente)
            }, {
                hideProgress()
            }))
        }
    }

}
