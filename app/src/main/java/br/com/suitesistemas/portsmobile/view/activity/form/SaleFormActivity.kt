package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.databinding.ActivitySaleFormBinding
import br.com.suitesistemas.portsmobile.model.ProductDetail
import br.com.suitesistemas.portsmobile.model.entity.Customer
import br.com.suitesistemas.portsmobile.model.entity.Product
import br.com.suitesistemas.portsmobile.model.entity.ProductColor
import br.com.suitesistemas.portsmobile.model.entity.Sale
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.PeopleSearchActivity
import br.com.suitesistemas.portsmobile.view.activity.search.SelectProductSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.SaleProductAdapter
import br.com.suitesistemas.portsmobile.view.dialog.ProductQuantityDialog
import br.com.suitesistemas.portsmobile.viewModel.form.SaleFormViewModel
import kotlinx.android.synthetic.main.activity_sale_form.*
import java.util.*
import kotlin.math.roundToLong

class SaleFormActivity : FormActivity<Sale>(R.menu.menu_sale_form, R.id.menu_search_products) {

    companion object {
        private const val PEOPLE_SELECTED_CODE = 1
        private const val PRODUCT_SELECTED_CODE = 2
    }
    lateinit var viewModel: SaleFormViewModel
    private lateinit var saleProductAdapter: SaleProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_form)

        configureActionBar(R.string.cadastro_venda)
        initViewModel()
        configureDataBinding()
    }

    override fun onResume() {
        super.onResume()
        findLoggedUser()
        fetchCompanies()
        fetchConditions()
        configureList()
        configureButton()
        initValuesToEdit()
    }

    override fun getBtnConfirm() = sale_form_btn_confirm
    override fun getProgressBar() = sale_form_progressbar
    override fun getLayout() = sale_form

    private fun initViewModel() {
        val sharedPref = getSharedPreferences("userResponse", Context.MODE_PRIVATE)
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(SaleFormViewModel::class.java)
        viewModel.initRepositories(companyName, sharedPref.getInt("codigo", 0))
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivitySaleFormBinding>(this, R.layout.activity_sale_form)
            .apply {
                lifecycleOwner = this@SaleFormActivity
                viewModel = this@SaleFormActivity.viewModel
            }
    }

    private fun findLoggedUser() {
        with (viewModel) {
            findLoggedUser()
            userResponse.observe(this@SaleFormActivity, observerHandler({
                user = it
                val saleToEdit = intent.getParcelableExtra<Sale>("sale")
                if (saleToEdit == null) {
                    val value = sale.value!!
                    value.fky_vendedor = it.fky_pessoa
                    sale.value = value
                }
            }, {
                showMessage(it, R.string.nao_encontrou_usuario)
            }, {
                hideProgress()
            }))
        }
    }

    private fun fetchCompanies() {
        with (viewModel) {
            fetchAllCompanies()
            companiesResponse.observe(this@SaleFormActivity, observerHandler({
                addAllCompanies(it)
                val companiesName = getCompaniesNames()
                val index = getCompanyIndex()
                sale_form_company.setAdapterAndSelection(this@SaleFormActivity, companiesName, index)
            }, {
                showMessage(it, R.string.nao_encontrou_empresas)
            }, {
                hideProgress()
            }))
        }
    }

    private fun fetchConditions() {
        with (viewModel) {
            fetchAllPaymentConditions()
            paymentConditionResponse.observe(this@SaleFormActivity, observerHandler({
                val conditionsName = getConditionsNames()
                val index = getPaymentConditionIndexBySale()
                sale_form_payment_condition.setAdapterAndSelection(this@SaleFormActivity, conditionsName, index)
            }, {
                showMessage(it, R.string.nao_encontrou_condicoes)
            }, {
                hideProgress()
            }))
        }
    }

    private fun initValuesToEdit() {
        val saleToEdit = intent.getParcelableExtra<Sale>("sale")
        if (saleToEdit != null) {
            showProgress()
            with (viewModel) {
                findItemBy(saleToEdit)
                itemResponse.observe(this@SaleFormActivity, observerHandler({
                    allAllItems(it)
                    sale.value = Sale(saleToEdit)
                    edit()
                }, {
                    showMessage(it, R.string.nao_encontrou_itens_venda)
                }, {
                    hideProgress()
                }))
            }
        } else {
            configureForm()
        }
    }

    override fun onClickedMenu() {
        executeAfterLoaded {
            startActivityForResult(Intent(this, SelectProductSearchActivity::class.java), PRODUCT_SELECTED_CODE)
        }
    }

    override fun onTimeSet() {
        viewModel.sale.value?.hor_emissao = calendar.time
        sale_form_hour.setText(calendar.toStringFormat("HH:mm:ss"))
    }

    private fun configureList() {
        saleProductAdapter = SaleProductAdapter(baseContext, viewModel.products) { onRemove(it) }
        with (sale_form_products) {
            adapter = saleProductAdapter
            addSwipe(SwipeToDeleteCallback(baseContext) { onRemove(it) })
        }
    }

    private fun onRemove(position: Int) {
        viewModel.removeProductBy(position)
        saleProductAdapter.notifyDataSetChanged()
        configureTotals()
    }

    private fun configureForm() {
        sale_form_date.setText(calendar.toStringFormat())
        sale_form_company.onItemSelected { viewModel.sale.value!!.fky_empresa = viewModel.companies[it] }
        initTimeDialog()
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
        sale_form_payment_condition.onItemSelected {
            with (viewModel.sale.value!!) {
                fky_condicao_pagamento = viewModel.conditions[it]
                dbl_total_produtos = viewModel.getTotalItems(it)
                configureTotals()
            }
        }
    }

    private fun configurePayedValueField() {
        sale_form_payed_value.addNumberMask()
        sale_form_payed_value.afterTextChanged { text ->
            var value  = 0.0
            if (text.isNotEmpty())
                value = text.toDoubleValue()
            calculateChange(value)
        }
    }

    private fun configureAdditionField() {
        sale_form_addition.addNumberMask()
        sale_form_addition.afterTextChanged { text ->
            var addition  = 0.0
            if (text.isNotEmpty())
                addition = text.toDoubleValue()
            val discount = sale_form_discount.toDoubleValue()
            calculateTotalSale(addition, discount)
        }
    }

    private fun configureDiscountField() {
        sale_form_discount.addNumberMask()
        sale_form_discount.afterTextChanged { text ->
            var discount  = 0.0
            if (text.isNotEmpty())
                discount = text.toDoubleValue()
            val addition = sale_form_addition.toDoubleValue()
            calculateTotalSale(addition, discount)
        }
    }

    private fun calculateTotalSale(addition: Double, discount: Double) {
        val totalItems = viewModel.sale.value?.dbl_total_produtos!!
        val saleValue = (totalItems + addition) - discount
        val valueWithTwoDigits = (saleValue * 10.0).roundToLong() / 10.0
        viewModel.sale.value?.dbl_total_venda = valueWithTwoDigits
        sale_form_total_sale.setText(valueWithTwoDigits.toStringValue())

        val payedValue = sale_form_payed_value.toDoubleValue()
        calculateChange(payedValue)
    }

    private fun calculateChange(payedValue: Double) {
        val change = sale_form_total_sale.toDoubleValue() - payedValue
        val changeWithTwoDigits = (change * 10.0).roundToLong() / 10.0
        sale_form_change.setText(changeWithTwoDigits.toStringValue())
    }

    private fun configureButton() {
        sale_form_btn_confirm.setOnClickListener {
            executeAfterLoaded {
                hideKeyboard()
                sale_form_client.error = null
                sale_form_salesman.error = null

                try {
                    viewModel.validateForm()
                    showProgress()

                    with(viewModel.sale.value!!) {
                        dbl_total_produtos = sale_form_total_items.toDoubleValue()
                        dbl_total_venda = sale_form_total_sale.toDoubleValue()
                        dbl_valor_pago = sale_form_payed_value.toDoubleValue()

                        val firebaseToken = FirebaseUtils.getToken(this@SaleFormActivity)

                        if (num_codigo_online.isEmpty())
                             insertSale(firebaseToken)
                        else updateSale(firebaseToken)
                    }
                } catch (ex: InvalidValueException) {
                    when (ex.field) {
                        "Cliente" -> sale_form_client.error = ex.message
                        "Vendedor" -> sale_form_salesman.error = ex.message
                        else -> showMessage(ex.message!!)
                    }
                }
            }
        }
    }

    private fun insertSale(firebaseToken: String) {
        with (viewModel) {
            insert(firebaseToken)
            insertResponse.observe(this@SaleFormActivity, observerHandler({
                sale.value = Sale(it)
                if (itemsIsFilled()) {
                    this@SaleFormActivity.insertItems()
                } else {
                    hideProgress()
                    setSaleResult()
                }
            }, {
                handleError(it, R.string.falha_inserir_venda)
            }))
        }
    }

    private fun insertItems() {
        with (viewModel) {
            insertItems()
            itemInsertResponse.observe(this@SaleFormActivity, observerHandler({
                setSaleResult()
            }, {
                showMessage(it, R.string.falha_inserir_itens_venda)
            }, {
                hideProgress()
            }))
        }
    }

    private fun updateSale(firebaseToken: String) {
        with (viewModel) {
            update(firebaseToken)
            updateResponse.observe(this@SaleFormActivity, observerHandler({
                sale.value?.version = it.version
                if (removedItemsIsFilled()) {
                    this@SaleFormActivity.deleteItems()
                } else {
                    this@SaleFormActivity.updateItems()
                }
            }, {
                handleError(it, R.string.falha_atualizar_venda)
            }))
        }
    }

    private fun updateItems() {
        with (viewModel) {
            updateItems()
            itemUpdateResponse.observe(this@SaleFormActivity, observerHandler({
                setSaleResult()
            }, {
                showMessage(it, R.string.falha_atualizar_itens_venda)
            }, {
                hideProgress()
            }))
        }
    }

    private fun deleteItems() {
        with (viewModel) {
            deleteItems()
            itemDeleteResponse.observe(this@SaleFormActivity, observerHandler({
                clearRemovedItem()
                Handler().postDelayed({ this@SaleFormActivity.updateItems() }, 200)
            }, {
                handleError(it, R.string.falha_remover_itens_venda)
            }))
        }
    }

    private fun setSaleResult() {
        val data = Intent()
        data.putExtra("sale_response", viewModel.sale.value)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun initSearchActivity(type: String) {
        executeAfterLoaded {
            val intent = Intent(this, PeopleSearchActivity::class.java)
            intent.putExtra("type", type)
            startActivityForResult(intent, PEOPLE_SELECTED_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PEOPLE_SELECTED_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val people = it.getParcelableExtra("people_response") as Customer
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
    }

    override fun onPostResume() {
        super.onPostResume()
        if (viewModel.selectedProducts()) {
            showProgress()
            viewModel.initProductColors { selected, colors -> showQuantityDialog(selected, colors) }
        } else {
            sale_form_btn_confirm.showFromBottom()
        }
    }

    private fun showQuantityDialog(selected: MutableList<Product>, colors: MutableList<ProductColor>) {
        hideProgress()
        ProductQuantityDialog
            .newInstance(selected, colors, {
                addProductsAndQuantities(it)
            }, {
                viewModel.clearSelectedProducts()
            })
            .show(supportFragmentManager)
    }

    private fun addProductsAndQuantities(productsDetails: LinkedHashMap<Product, ProductDetail>) {
        productsDetails.forEach {
            var product = viewModel.products.keys.find { prod -> prod.num_codigo_online == it.key.num_codigo_online }
            if (product == null)
                product = it.key
            viewModel.products[product] = it.value
        }
        viewModel.clearSelectedProducts()
        configureList()
        configureTotals()
        Handler().postDelayed({
            hideKeyboard()
            sale_form_btn_confirm.showFromBottom()
        }, 150)
    }

    private fun configureTotals() {
        val totalItems = viewModel.getTotalItems(sale_form_payment_condition.selectedItemPosition)
        val totalQuantity = viewModel.getTotalQuantity()

        with (viewModel.sale.value!!) {
            dbl_total_produtos = totalItems
            dbl_total_venda = totalItems
        }

        sale_form_total_items.setText(totalItems.toStringValue())
        sale_form_total_sale.setText(totalItems.toStringValue())
        sale_form_quantity.setText(totalQuantity.toString())
        calculateChange(totalItems)
        calculateTotalSale(sale_form_addition.toDoubleValue(), sale_form_discount.toDoubleValue())

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
        with (viewModel.sale.value!!) {
            sale_form_date.setText(calendar.toStringFormat("dd/MM/yyyy", dat_emissao))
            sale_form_hour.setText(calendar.toStringFormat("HH:mm:ss", hor_emissao!!))
            sale_form_client.setText(fky_cliente.dsc_nome_pessoa)
            sale_form_salesman.setText(fky_vendedor.dsc_nome_pessoa)
            sale_form_obs.setText(dsc_observacao)

            Handler().postDelayed({
                sale_form_payed_value.setText(dbl_valor_pago.toStringValue())
                sale_form_total_items.setText(dbl_total_produtos.toStringValue())
                sale_form_total_sale.setText(dbl_total_venda.toStringValue())

                with (viewModel) {
                    sale_form_company.setSelection(getCompanyIndexBySale())
                    sale_form_payment_condition.setSelection(getPaymentConditionIndexBySale())

                    convertItemsToProductDetail()
                    sale_form_quantity.setText(getTotalQuantity().toString())

                    if (dbl_total_produtos > 0)
                         enableTotals()
                    else disableTotals()

                    calculateChange(dbl_valor_pago)
                }
            }, 500)
        }

        Handler().postDelayed({
            configureForm()
            hideProgress()
        }, 1000)
    }

}
