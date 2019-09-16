package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.button.hideToBottom
import br.com.suitesistemas.portsmobile.custom.button.showFromBottom
import br.com.suitesistemas.portsmobile.custom.calendar.toStringFormat
import br.com.suitesistemas.portsmobile.custom.edit_text.afterTextChanged
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.progress_bar.hide
import br.com.suitesistemas.portsmobile.custom.progress_bar.show
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.custom.recycler_view.addSwipe
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.hideKeyboard
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.custom.view.showMessageError
import br.com.suitesistemas.portsmobile.databinding.ActivitySaleFormBinding
import br.com.suitesistemas.portsmobile.entity.*
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.ProductDetail
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.PeopleSearchActivity
import br.com.suitesistemas.portsmobile.view.activity.search.ProductSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ProductAdapter
import br.com.suitesistemas.portsmobile.view.adapter.SpinnerAdapter
import br.com.suitesistemas.portsmobile.view.dialog.ProductQuantityDialog
import br.com.suitesistemas.portsmobile.viewModel.form.SaleFormViewModel
import kotlinx.android.synthetic.main.activity_sale_form.*
import java.util.*
import kotlin.math.roundToLong

class SaleFormActivity : FormActivity(), TimePickerDialog.OnTimeSetListener {

    companion object {
        private const val PEOPLE_SELECTED_CODE = 1
        private const val PRODUCT_SELECTED_CODE = 2
    }
    lateinit var viewModel: SaleFormViewModel
    private val calendar = Calendar.getInstance()
    private lateinit var timeDialog: TimePickerDialog
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_form)

        configureActionBar(R.string.cadastro_venda)
        initViewModel()
        configureDataBinding()
        configureObservers()
        initForm()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        sale_form_btn_confirm.showFromBottom()
    }

    override fun onPause() {
        super.onPause()
        sale_form_btn_confirm.hideToBottom()
    }

    private fun init() {
        initTimeDialog()
        configureList()
        configureForm()
        configureButton()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(SaleFormViewModel::class.java)
        viewModel.initRepositories(companyName)
        viewModel.fetchAllCompanies()
        viewModel.fetchAllPaymentConditions()
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivitySaleFormBinding>(this, R.layout.activity_sale_form)
            .apply {
                lifecycleOwner = this@SaleFormActivity
                viewModel = this@SaleFormActivity.viewModel
            }
    }

    private fun configureObservers() {
        viewModel.companiesResponse.observe(this, getCompanyObserver())
        viewModel.paymentConditionResponse.observe(this, getPaymentConditionsObserver())
    }

    private fun getCompanyObserver(): Observer<ApiResponse<MutableList<Company>?>> {
        return Observer {
            sale_form_progressbar.hide()
            when (it.messageError) {
                null -> configureCompanyAdapter(it)
                else -> showMessageError(sale_form, "COMPANY FINDALL ERROR:", it.messageError!!,
                    getString(R.string.nao_encontrou_empresas))
            }
        }
    }

    private fun getPaymentConditionsObserver(): Observer<ApiResponse<MutableList<PaymentConditions>?>> {
        return Observer {
            sale_form_progressbar.hide()
            when (it.messageError) {
                null -> configurePaymentConditionsAdapter(it)
                else -> showMessageError(sale_form, "CONDITION FINDALL ERROR", it.messageError!!,
                    getString(R.string.nao_encontrou_condicoes))
            }
        }
    }

    private fun configureCompanyAdapter(response: ApiResponse<MutableList<Company>?>) {
        response.data?.let { companies ->
            viewModel.addAllCompanies(companies)
            val companiesName = companies.map { company -> company.dsc_empresa }
            val index = companies.indexOfFirst { company ->
                company.cod_empresa == viewModel.sale.value?.fky_empresa?.cod_empresa
            }
            sale_form_company.adapter = getSpinnerAdapter(companiesName)
            sale_form_company.setSelection(index)
        }
    }

    private fun configurePaymentConditionsAdapter(response: ApiResponse<MutableList<PaymentConditions>?>) {
        response.data?.let { conditions ->
            viewModel.addAllPaymentConditions(conditions)
            val conditionsName = conditions.map { condition -> condition.dsc_condicao_pagamento }
            val index = conditions.indexOfFirst { condition ->
                val conditionId = viewModel.sale.value?.fky_condicao_pagamento?.cod_condicao_pagamento!!
                if (conditionId > 0)
                    condition.cod_condicao_pagamento = conditionId
                condition.flg_a_vista == EYesNo.S
            }
            sale_form_payment_condition.adapter = getSpinnerAdapter(conditionsName)
            sale_form_payment_condition.setSelection(index)
        }
    }

    private fun getSpinnerAdapter(names: List<String>): ArrayAdapter<String> {
        val adapter = SpinnerAdapter(this, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun initForm() {
        val saleToEdit = intent.getSerializableExtra("sale")
        if (saleToEdit != null) {
            sale_form_progressbar.show()
            viewModel.findItemBy(saleToEdit as Sale)
            viewModel.itemResponse.observe(this, getItemObserver(saleToEdit))
        } else {
            init()
        }
    }

    private fun getItemObserver(saleToEdit: Sale): Observer<ApiResponse<MutableList<SaleItem>?>> {
        return Observer {
            sale_form_progressbar.hide()
            if (it.messageError == null) {
                it.data?.let { items ->
                    viewModel.allAllItems(items)
                    viewModel.sale.value?.copy(saleToEdit)
                    edit()
                }
            } else {
                showMessageError(sale_form, "SALEItem FINDALL ERROR:", it.messageError!!,
                    getString(R.string.nao_encontrou_itens_venda))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sale_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_search_products -> {
                startProductSearchActivity()
                super.onOptionsItemSelected(item)
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startProductSearchActivity() {
        executeAfterLoaded(sale_form_progressbar.isIndeterminate, sale_form) {
            startActivityForResult(Intent(this, ProductSearchActivity::class.java), PRODUCT_SELECTED_CODE)
        }
    }

    private fun initTimeDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        timeDialog = TimePickerDialog(this, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        viewModel.sale.value?.hor_emissao = calendar.time
        sale_form_hour.setText(calendar.toStringFormat("HH:mm:ss"))
    }

    private fun configureList() {
        productAdapter = ProductAdapter(baseContext, viewModel.products)
        configureSwipe()
        with (sale_form_products) {
            adapter = productAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }
    }

    private fun configureSwipe() {
        sale_form_products.addSwipe(SwipeToDeleteCallback(baseContext) { position ->
            viewModel.removeProductBy(position)
            productAdapter.notifyDataSetChanged()
            configureTotals()
        })
    }

    private fun configureForm() {
        sale_form_date.setText(calendar.toStringFormat())
        configureHourField()
        configureClientField()
        configureSalesmanField()
        configurePaymentConditionsField()
        configurePayedValueField()
        configureAdditionField()
        configureDiscountField()
    }

    private fun configureHourField() {
        if (viewModel.sale.value?.num_codigo_online.isNullOrEmpty())
            viewModel.sale.value?.hor_emissao = calendar.time
        sale_form_hour.setText(viewModel.sale.value?.hor_emissao?.toStringFormat("HH:mm:ss"))
        sale_form_hour.setOnClickListener { timeDialog.show() }
        sale_form_hour.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                timeDialog.show()
        }
    }

    private fun configureClientField() {
        sale_form_client.setOnClickListener { initSearchActivity("C") }
        sale_form_client.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                initSearchActivity("C")
        }
    }

    private fun configureSalesmanField() {
        sale_form_salesman.setOnClickListener { initSearchActivity("V") }
        sale_form_salesman.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                initSearchActivity("V")
        }
    }

    private fun configurePaymentConditionsField() {
        sale_form_payment_condition.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.sale.value?.dbl_total_produtos = viewModel.getTotalItems(position)
                configureTotals()
            }
        }
    }

    private fun configurePayedValueField() {
        sale_form_payed_value.afterTextChanged { text ->
            var value  = 0.0
            if (text.isNotEmpty())
                value = getDoubleValueFrom(text)
            calculateChange(value)
        }
    }

    private fun configureAdditionField() {
        sale_form_addition.afterTextChanged { text ->
            var addition  = 0.0
            if (text.isNotEmpty())
                addition = getDoubleValueFrom(text)
            val discount = getDoubleValueFrom(sale_form_discount)
            calculateTotalSale(addition, discount)
        }
    }

    private fun configureDiscountField() {
        sale_form_discount.afterTextChanged { text ->
            var discount  = 0.0
            if (text.isNotEmpty())
                discount = getDoubleValueFrom(text)
            val addition = getDoubleValueFrom(sale_form_addition)
            calculateTotalSale(addition, discount)
        }
    }

    private fun getDoubleValueFrom(editText: EditText): Double {
        var value = 0.0
        val valueText = editText.text.toString()
        if (valueText.isNotEmpty())
            value = getDoubleValueFrom(valueText)
        return value
    }

    private fun getDoubleValueFrom(valueText: String): Double {
        val value = valueText.toDouble()
        return (value * 10.0).roundToLong() / 10.0
    }

    private fun calculateTotalSale(addition: Double, discount: Double) {
        val totalItems = viewModel.sale.value?.dbl_total_produtos!!
        val saleValue = (totalItems + addition) - discount
        val valueWithTwoDigits = (saleValue * 10.0).roundToLong() / 10.0
        viewModel.sale.value?.dbl_total_venda = valueWithTwoDigits
        sale_form_total_sale.setText(valueWithTwoDigits.toString())

        val payedValue = getDoubleValueFrom(sale_form_payed_value)
        calculateChange(payedValue)
    }

    private fun calculateChange(payedValue: Double) {
        val change = getDoubleValueFrom(sale_form_total_sale) - payedValue
        val changeWithTwoDigits = (change * 10.0).roundToLong() / 10.0
        sale_form_change.setText(changeWithTwoDigits.toString())
    }

    private fun configureButton() {
        sale_form_btn_confirm.setOnClickListener {
            executeAfterLoaded(sale_form_progressbar.isIndeterminate, sale_form) {
                hideKeyboard()
                sale_form_client.error = null
                sale_form_salesman.error = null

                try {
                    val conditionPosition = sale_form_payment_condition.selectedItemPosition
                    val companyPosition = sale_form_company.selectedItemPosition

                    viewModel.validateForm(conditionPosition, companyPosition)
                    sale_form_progressbar.show()
                    viewModel.sale.value?.dbl_total_produtos = getDoubleValueFrom(sale_form_total_items)
                    viewModel.sale.value?.dbl_total_venda = getDoubleValueFrom(sale_form_total_sale)
                    viewModel.sale.value?.dbl_valor_pago = getDoubleValueFrom(sale_form_payed_value)
                    val firebaseToken = FirebaseUtils.getToken(this)
                    viewModel.save(firebaseToken)
                    viewModel.insertResponse.observe(this, getSaleInsertObserver())
                    viewModel.updateResponse.observe(this, getSaleUpdateObserver())
                } catch (ex: InvalidValueException) {
                    when (ex.field) {
                        "Cliente" -> sale_form_client.error = ex.message
                        "Vendedor" -> sale_form_salesman.error = ex.message
                        else -> showMessage(sale_form, ex.message!!)
                    }
                }
            }
        }
    }

    private fun getSaleInsertObserver(): Observer<ApiResponse<Sale?>> {
        return Observer {
            if (it.messageError == null) {
                it?.data?.let { sale -> viewModel.sale.value?.copy(sale) }
                if (viewModel.itemsIsFilled()) {
                    viewModel.insertItems()
                    viewModel.itemInsertResponse.observe(this, getItemsInsertObserver())
                } else {
                    sale_form_progressbar.hide()
                    setSaleResult()
                }
            } else {
                showMessage(sale_form, it.messageError, getString(R.string.falha_inserir_venda))
                sale_form_progressbar.hide()
            }
        }
    }

    private fun getItemsInsertObserver(): Observer<ApiResponse<MutableList<SaleItem>?>> {
        return Observer {
            sale_form_progressbar.hide()
            if (it.messageError == null) {
                setSaleResult()
            } else {
                showMessage(sale_form, it.messageError, getString(R.string.falha_inserir_itens_venda))
            }
        }
    }

    private fun getSaleUpdateObserver(): Observer<ApiResponse<VersionResponse?>> {
        return Observer {
            if (it.messageError == null) {
                it?.data?.let { versionResponse -> viewModel.sale.value?.version = versionResponse.version }
                if (viewModel.itemsIsFilled()) {
                    viewModel.updateItems()
                    viewModel.itemUpdateResponse.observe(this, getItemsUpdateObserver())
                } else {
                    sale_form_progressbar.hide()
                    setSaleResult()
                }
            } else {
                showMessage(sale_form, it.messageError, getString(R.string.falha_atualizar_venda))
                sale_form_progressbar.hide()
            }
        }
    }

    private fun getItemsUpdateObserver(): Observer<ApiResponse<Any?>> {
        return Observer {
            sale_form_progressbar.hide()
            if (it.messageError == null) {
                if (viewModel.removedItemsIsFilled()) {
                    sale_form_progressbar.show()
                    viewModel.deleteItems()
                    viewModel.itemDeleteResponse.observe(this, getItemsDeleteObserver())
                } else {
                    setSaleResult()
                }
            } else {
                showMessage(sale_form, it.messageError, getString(R.string.falha_atualizar_itens_venda))
            }
        }
    }

    private fun getItemsDeleteObserver(): Observer<ApiResponse<Any?>> {
        return Observer {
            sale_form_progressbar.hide()
            if (it.messageError == null) {
                viewModel.clearRemovedItem()
                setSaleResult()
            } else {
                showMessageError(sale_form, "SALEItem DELETE ERROR:", it.messageError!!,
                    getString(R.string.falha_remover_itens_venda))
            }
        }
    }

    private fun setSaleResult() {
        val data = Intent()
        data.putExtra("sale_response", viewModel.sale.value)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun initSearchActivity(type: String) {
        executeAfterLoaded(sale_form_progressbar.isIndeterminate, sale_form) {
            val intent = Intent(this, PeopleSearchActivity::class.java)
            intent.putExtra("type", type)
            startActivityForResult(intent, PEOPLE_SELECTED_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PEOPLE_SELECTED_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val people = it.getSerializableExtra("people_response") as Customer
                    val type = it.getStringExtra("type")
                    setPeopleName(people, type)
                }
            }
        } else if (requestCode == PRODUCT_SELECTED_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.extras?.let { bundle ->
                    val productsSelected = bundle.getParcelableArrayList<Product>("products_selected")
                    viewModel.addSelectedProducts(productsSelected)
                }
            }
        }
        hideKeyboard()
        sale_form_btn_confirm.showFromBottom()
    }

    override fun onPostResume() {
        super.onPostResume()
        if (viewModel.selectedProducts())
            viewModel.initProductColors { selected, colors -> showQuantityDialog(selected, colors) }
    }

    private fun showQuantityDialog(selected: MutableList<Product>, colors: MutableList<ProductColor>) {
        ProductQuantityDialog
            .newInstance(selected, colors) { addProductsAndQuantities(it) }
            .show(supportFragmentManager)
    }

    private fun addProductsAndQuantities(productsDetails: LinkedHashMap<Product, ProductDetail>) {
        productsDetails.forEach {
            var product = viewModel.products.keys.find { prod -> prod.cod_produto == it.key.cod_produto }
            if (product == null)
                product = it.key
            viewModel. products[product] = it.value
        }
        viewModel.clearSelectedProducts()
        configureList()
        configureTotals()
        Handler().postDelayed({ hideKeyboard() }, 150)
    }

    private fun configureTotals() {
        val totalItems = viewModel.getTotalItems(sale_form_payment_condition.selectedItemPosition)
        val totalQuantity = viewModel.getTotalQuantity()

        viewModel.sale.value?.dbl_total_produtos = totalItems
        viewModel.sale.value?.dbl_total_venda = totalItems

        sale_form_total_items.setText(totalItems.toString())
        sale_form_total_sale.setText(totalItems.toString())
        sale_form_quantity.setText(totalQuantity.toString())
        calculateChange(totalItems)
        calculateTotalSale(getDoubleValueFrom(sale_form_addition), getDoubleValueFrom(sale_form_discount))

        if (totalItems > 0)
             enableTotals()
        else disableTotals()
    }

    private fun enableTotals() {
        sale_form_payed_value.isEnabled = true
        sale_form_addition.isEnabled = true
        sale_form_discount.isEnabled = true
    }

    private fun disableTotals() {
        sale_form_payed_value.isEnabled = false
        sale_form_addition.isEnabled = false
        sale_form_discount.isEnabled = false
    }

    private fun setPeopleName(people: Customer, type: String) {
        when (type) {
            "C" -> {
                sale_form_client.setText(people.dsc_nome_pessoa)
                viewModel.sale.value?.fky_cliente = people
            }
            "V" -> {
                sale_form_salesman.setText(people.dsc_nome_pessoa)
                viewModel.sale.value?.fky_vendedor = people
            }
        }
    }

    private fun edit() {
        sale_form_date.setText(calendar.toStringFormat("dd/MM/yyyy", viewModel.sale.value?.dat_emissao!!))
        sale_form_hour.setText(calendar.toStringFormat("HH:mm:ss", viewModel.sale.value?.hor_emissao!!))
        sale_form_client.setText(viewModel.sale.value?.fky_cliente?.dsc_nome_pessoa)
        sale_form_salesman.setText(viewModel.sale.value?.fky_vendedor?.dsc_nome_pessoa)
        sale_form_obs.setText(viewModel.sale.value?.dsc_observacao)

        sale_form_company.setSelection(viewModel.getCompanyIndexBySale())
        sale_form_payment_condition.setSelection(viewModel.getPaymentConditionIndexBySale())

        sale_form_total_items.setText(viewModel.sale.value?.dbl_total_produtos.toString())
        sale_form_total_sale.setText(viewModel.sale.value?.dbl_total_venda.toString())
        sale_form_payed_value.setText(viewModel.sale.value?.dbl_valor_pago.toString())

        viewModel.convertItemsToProductDetail()
        sale_form_quantity.setText(viewModel.getTotalQuantity().toString())

        if (viewModel.sale.value?.dbl_total_produtos!! > 0)
            enableTotals()
        else disableTotals()
        calculateChange(viewModel.sale.value?.dbl_valor_pago!!)

        Handler().postDelayed({
            init()
            sale_form_progressbar.hide()
        }, 250)
    }

}
