package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.edit_text.addCnpjMask
import br.com.suitesistemas.portsmobile.custom.edit_text.addCpfMask
import br.com.suitesistemas.portsmobile.custom.edit_text.addPhoneMask
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.observer.observerHandler
import br.com.suitesistemas.portsmobile.custom.progress_bar.hide
import br.com.suitesistemas.portsmobile.custom.progress_bar.show
import br.com.suitesistemas.portsmobile.custom.spinner.onItemSelected
import br.com.suitesistemas.portsmobile.custom.spinner.setAdapterAndSelection
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.hideKeyboard
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.databinding.ActivityCustomerFormBinding
import br.com.suitesistemas.portsmobile.entity.Company
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.model.enums.ECustomerSituation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.AutoCompleteAdapter
import br.com.suitesistemas.portsmobile.view.adapter.SpinnerAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.CustomerFormViewModel
import kotlinx.android.synthetic.main.activity_customer_form.*

class CustomerFormActivity : FormActivity() {

    private lateinit var viewModel: CustomerFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_form)

        configureActionBar(R.string.cadastro_cliente)
        initViewModel()
        configureObservers()
        configureDataBinding()
        configureForm()
        initSituationsList()
        configureBtnConfirm()
    }

    override fun getBtnConfirm() = customer_form_btn_confirm

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(CustomerFormViewModel::class.java)
        viewModel.initRepositories(companyName)
        viewModel.fetchAllCompanies()
    }

    private fun configureObservers() {
        viewModel.selectedType.observe(this, Observer<String> {
            it?.let { type -> viewModel.peopleTypeSelected(type)}
        })
        viewModel.companiesResponse.observe(this, observerHandler({
            configureCompanyAdapter(it)
        }, {
            showMessage(customer_form, it, getString(R.string.nao_encontrou_empresas))
        }, {
            customer_form_progressbar.hide()
        }))
    }

    private fun configureCompanyAdapter(companies: MutableList<Company>) {
        viewModel.addAllCompanies(companies)
        val companiesName = companies.map { company -> company.dsc_empresa }
        val index = companies.indexOfFirst { company ->
            company.cod_empresa == viewModel.customer.value?.fky_empresa?.cod_empresa
        }
        customer_form_company.setAdapterAndSelection(this, companiesName, index)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityCustomerFormBinding>(this, R.layout.activity_customer_form)
            .apply {
                lifecycleOwner = this@CustomerFormActivity
                viewModel = this@CustomerFormActivity.viewModel
            }
    }

    private fun configureForm() {
        val customerToEdit = intent.getParcelableExtra<Customer>("customer")
        if (customerToEdit != null)
            viewModel.concat(customerToEdit as Customer)
        configureTypeAdapter()
        configureUFAdapter()
        configureStateInscs()
        customer_form_cell_phone.addPhoneMask()
        customer_form_phone.addPhoneMask()
    }

    private fun configureTypeAdapter() {
        with (customer_form_type) {
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

    private fun configureUFAdapter() {
        val adapter = AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, viewModel.ufs)
        customer_form_state.setAdapter(adapter)
    }
    
    private fun configureStateInscs() {
        if (viewModel.selectedType.value == "JURÍDICA") {
            val adapter = AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, viewModel.inscs)
            customer_form_insc_estadual.setAdapter(adapter)
        } else {
            customer_form_insc_estadual.setAdapter(null)
        }
    }

    private fun initSituationsList() {
        val situations = resources.getStringArray(R.array.customerSituations)
        val customerSituation = viewModel.customer.value?.flg_situacao_cliente
        customer_form_situation.setAdapterAndSelection(this, situations.toList(), when (customerSituation) {
            ECustomerSituation.N -> 0
            ECustomerSituation.A -> 1
            ECustomerSituation.P -> 2
            ECustomerSituation.C -> 3
            else -> 1
        })
    }

    private fun configureBtnConfirm() {
        customer_form_btn_confirm.setOnClickListener {
            executeAfterLoaded(customer_form_progressbar.isIndeterminate, customer_form) {
                hideKeyboard()
                customer_form_name.error = null
                customer_form_phone.error = null
                customer_form_cell_phone.error = null

                try {
                    val firebaseToken = FirebaseUtils.getToken(this)
                    val situation = customer_form_situation.selectedItem.toString()
                    val companyPosition = customer_form_company.selectedItemPosition
                    viewModel.validateForm(situation, companyPosition)

                    customer_form_progressbar.show()
                    viewModel.save(firebaseToken)
                    configureInsertObserver()
                    configureUpdateObserver()
                } catch (ex: InvalidValueException) {
                    when (ex.field) {
                        "Nome" -> customer_form_name.error = ex.message
                        "Telefone" -> customer_form_phone.error = ex.message
                        "Celular" -> customer_form_cell_phone.error = ex.message
                        "Insc" -> customer_form_insc_estadual.error = ex.message
                        "UF" -> customer_form_state.error = ex.message
                        else -> showMessage(customer_form, ex.message!!)
                    }
                }
            }
        }
    }

    private fun configureInsertObserver() {
        viewModel.insertResponse.observe(this, observerHandler({
            viewModel.customer.value = it
            created(it)
        }, {
            showMessage(customer_form, it, getString(R.string.falha_inserir_cliente))
        }, {
            customer_form_progressbar.hide()
        }))
    }

    private fun configureUpdateObserver() {
        viewModel.updateResponse.observe(this, observerHandler({
            viewModel.customer.value?.version = it.version
            created(viewModel.customer.value!!)
        }, {
            showMessage(customer_form, it, getString(R.string.falha_atualizar_cliente))
        }, {
            customer_form_progressbar.hide()
        }))
    }

    private fun created(customer: Customer) {
        val data = Intent()
        data.putExtra("customer_response", customer)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
