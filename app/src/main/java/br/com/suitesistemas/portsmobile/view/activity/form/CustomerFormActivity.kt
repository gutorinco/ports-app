package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.view.hideKeyboard
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.databinding.ActivityCustomerFormBinding
import br.com.suitesistemas.portsmobile.entity.Company
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.enums.ECustomerSituation
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.viewModel.form.CustomerFormViewModel
import com.google.android.material.snackbar.Snackbar
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

        val customerToEdit = intent.getSerializableExtra("customer")
        if (customerToEdit != null)
            viewModel.concat(customerToEdit as Customer)

        initSituationsList()
        configureBtnConfirm()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(CustomerFormViewModel::class.java)
        viewModel.initRepositories(companyName)
        viewModel.fetchAllCompanies()
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
        viewModel.companiesResponse.observe(this, getCompanyObserver())
    }

    private fun getCompanyObserver(): Observer<ApiResponse<MutableList<Company>?>> {
        return Observer {
            customer_form_progressbar.isIndeterminate = false
            if (it.messageError == null) {
                configureCompanyAdapter(it)
            } else {
                Log.e("COMPANY FINDALL ERROR:", it.messageError)
                Snackbar.make(customer_form, getString(R.string.nao_encontrou_empresas), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun configureCompanyAdapter(response: ApiResponse<MutableList<Company>?>) {
        response.data?.let { companies ->
            viewModel.addAllCompanies(companies)
            val companiesName = companies.map { company -> company.dsc_empresa }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, companiesName)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val index = companies.indexOfFirst { company ->
                company.cod_empresa == viewModel.customer.value?.fky_empresa?.cod_empresa
            }
            customer_form_company.adapter = adapter
            customer_form_company.setSelection(index)
        }
    }

    private fun initSituationsList() {
        val situations = resources.getStringArray(R.array.customerSituations)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, situations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        customer_form_situation.adapter = adapter
        customer_form_situation.setSelection(when (viewModel.customer.value?.flg_situacao_cliente) {
            ECustomerSituation.N -> 0
            ECustomerSituation.A -> 1
            ECustomerSituation.P -> 2
            ECustomerSituation.C -> 3
            else -> 1
        })
    }

    private fun configureBtnConfirm() {
        customer_form_btn_confirm.setOnClickListener {
            when {
                customer_form_progressbar.isIndeterminate -> showMessage(customer_form, getString(R.string.aguarde_terminar))
                else -> {
                    hideKeyboard()
                    customer_form_name.error = null
                    customer_form_phone.error = null
                    customer_form_cell_phone.error = null

                    try {
                        val situation = customer_form_situation.selectedItem.toString()
                        val companyPosition = customer_form_company.selectedItemPosition

                        viewModel.validateForm(situation, companyPosition)
                        customer_form_progressbar.isIndeterminate = true
                        viewModel.save()
                        viewModel.insertResponse.observe(this, getInsertObserver())
                        viewModel.updateResponse.observe(this, getUpdateObserver())
                    } catch (ex: InvalidValueException) {
                        when (ex.field) {
                            "Nome" -> customer_form_name.error = ex.message
                            "Telefone" -> customer_form_phone.error = ex.message
                            "Celular" -> customer_form_cell_phone.error = ex.message
                            else -> showMessage(customer_form, ex.message!!)
                        }
                    }
                }
            }
        }
    }

    private fun getInsertObserver(): Observer<ApiResponse<Customer?>> {
        return Observer {
            customer_form_progressbar.isIndeterminate = false
            if (it.messageError == null) {
                it?.data?.let { customer ->
                    viewModel.customer.value = customer
                    created(customer)
                }
            } else {
                Log.e("CUSTOMER INSERT ERROR:", it.messageError)
                Snackbar.make(customer_form, getString(R.string.falha_inserir_cliente), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun getUpdateObserver(): Observer<ApiResponse<VersionResponse?>> {
        return Observer {
            customer_form_progressbar.isIndeterminate = false
            if (it.messageError == null) {
                it?.data?.let { versionResponse ->
                    viewModel.customer.value?.version = versionResponse.version
                    created(viewModel.customer.value!!)
                }
            } else {
                Snackbar.make(customer_form, getString(R.string.falha_atualizar_cliente), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun created(customer: Customer) {
        val data = Intent()
        data.putExtra("customer_response", customer)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
