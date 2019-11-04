package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.databinding.ActivityOrderFormBinding
import br.com.suitesistemas.portsmobile.entity.*
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.ModelSearchActivity
import br.com.suitesistemas.portsmobile.view.activity.search.PeopleSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.OrderItemAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.OrderFormViewModel
import kotlinx.android.synthetic.main.activity_order_form.*
import java.util.*
import kotlin.math.roundToLong

class OrderFormActivity : FormActivity<Order>(R.menu.menu_order_form, R.id.menu_search_models),
    OnItemClickListener {

    companion object {
        private const val PEOPLE_SELECTED_CODE = 1
        private const val MODEL_SELECTED_CODE = 2
        private const val BUILD_GRID_CODE = 3
        private const val CHANGE_GRID_CODE = 4
    }

    lateinit var viewModel: OrderFormViewModel
    private var oldDiscount: Double = 0.0
    private val thisActivity = this@OrderFormActivity
    private lateinit var orderItemAdapter: OrderItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_form)

        configureActionBar(R.string.cadastro_pedido)
        initViewModel()
        configureDataBinding()
    }

    override fun onResume() {
        super.onResume()
        configureObservers()
        initForm()
        configureBtnConfirm()
    }

    override fun getBtnConfirm() = order_form_btn_confirm
    override fun getProgressBar() = order_form_progressbar
    override fun getLayout() = order_form

    private fun initViewModel() {
        val sharedPref = getSharedPreferences("userResponse", Context.MODE_PRIVATE)
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(OrderFormViewModel::class.java)
        viewModel.initRepositories(companyName, sharedPref.getInt("codigo", 0))
    }

    private fun configureDataBinding() {
        DataBindingUtil.setContentView<ActivityOrderFormBinding>(this, R.layout.activity_order_form)
            .apply {
                lifecycleOwner = thisActivity
                viewModel = thisActivity.viewModel
                doubleUtils = DoubleUtils()
            }
    }

    private fun configureObservers() {
        fetchCombinations()
        fecthUser()
        fetchCompanies()
        fetchPaymentConditions()
    }

    private fun fetchCombinations() {
        with(viewModel) {
            fetchAllCombinations()
            combinationResponse.observe(thisActivity, observerHandler({
                addAllCombinations(it)
            }, {
                showMessage(it, R.string.nao_encontrou_combinacoes)
            }))
        }
    }

    private fun fecthUser() {
        val orderToEdit = intent.getParcelableExtra<Order>("order")
        if (orderToEdit == null) {
            with(viewModel) {
                findLoggedUser()
                userResponse.observe(thisActivity, observerHandler({
                    val order = viewModel.order.value!!
                    order.fky_representante = it.fky_pessoa
                    viewModel.order.value = order
                }, {
                    showMessage(it, R.string.nao_encontrou_usuario)
                }, {
                    hideProgress()
                }))
            }
        }
    }

    private fun fetchCompanies() {
        with(viewModel) {
            fetchAllCompanies()
            companiesResponse.observe(thisActivity, observerHandler({
                addAllCompanies(it)
                order_form_company.setAdapterAndSelection(this@OrderFormActivity, getCompaniesNames(), getCompanyIndex())
            }, {
                showMessage(it, R.string.nao_encontrou_empresas)
            }, {
                hideProgress()
            }))
        }
    }

    private fun fetchPaymentConditions() {
        with(viewModel) {
            fetchAllPaymentConditions()
            paymentConditionResponse.observe(thisActivity, observerHandler({
                addAllPaymentConditions(it)
                val conditionsName = getPaymentConditionsNames()
                val index = getPaymentConditionIndex()
                order_form_payment_condition.setAdapterAndSelection(this@OrderFormActivity, conditionsName, index)
            }, {
                showMessage(it, R.string.nao_encontrou_condicoes)
            }, {
                hideProgress()
            }))
        }
    }

    private fun initForm() {
        val orderToEdit = intent.getParcelableExtra<Order>("order")
        if (orderToEdit != null) {
            showProgress()
            oldDiscount = orderToEdit.dbl_total_item - orderToEdit.dbl_total_pedido
            viewModel.order.value = Order(orderToEdit)
            fetchItems(orderToEdit)
        } else {
            init()
        }
    }

    private fun fetchItems(orderToEdit: Order) {
        with(viewModel) {
            findItemsBy(orderToEdit)
            itemResponse.observe(thisActivity, observerHandler({
                addAllItems(it)
                fetchGrids(orderToEdit)
            }, {
                init()
                handleError(it, R.string.nao_encontrou_itens_pedido)
            }))
        }
    }

    private fun fetchGrids(orderToEdit: Order) {
        with(viewModel) {
            findGridsBy(orderToEdit)
            gridResponse.observe(thisActivity, observerHandler({
                addAllGrids(it)
                init()
                edit()
            }, {
                init()
                handleError(it, R.string.nao_encontrou_itens_pedido)
            }))
        }
    }

    private fun init() {
        initTimeDialog()
        configureList()
        configureForm()
    }

    override fun onTimeSet() {
        viewModel.order.value?.hor_emissao = calendar.time
        order_form_hour.setText(calendar.toStringFormat("HH:mm:ss"))
    }

    private fun getAdapter(): OrderItemAdapter {
        return OrderItemAdapter(baseContext, viewModel.getItems(), viewModel.getGrids(), {
            onRemove(it)
        }, {
            onItemClicked(it, order_form_items)
        })
    }

    private fun configureList() {
        orderItemAdapter = getAdapter()
        with(order_form_items) {
            adapter = orderItemAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            addOnItemClickListener(thisActivity)
            addSwipe(SwipeToDeleteCallback(baseContext) { onRemove(it) })
        }
        configureEmptyView()
    }

    private fun onRemove(position: Int) {
        viewModel.removeItemBy(position)
        orderItemAdapter.setAdapter(viewModel.getItems())
        configureTotals()
        configureEmptyView()
    }

    private fun configureEmptyView() {
        if (viewModel.itemsIsEmpty() && viewModel.newItemsIsEmpty()) {
            order_form_empty_view.visibility = View.VISIBLE
            order_form_items.visibility = View.GONE
        } else {
            order_form_empty_view.visibility = View.GONE
            order_form_items.visibility = View.VISIBLE
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        val item = orderItemAdapter.getItemBy(position)
        val grids = viewModel.getGridsBy(item.cod_sequencia!!)
        val model = item.fky_modelo
        val conditionPosition = order_form_payment_condition.selectedItemPosition
        if (!grids.isNullOrEmpty()) {
            with(Intent(this, OrderGridFormActivity::class.java)) {
                putExtra("model", model)
                putExtra("orderItem", item)
                putExtra("paymentCondition", viewModel.getPaymentCondition(conditionPosition))
                putParcelableArrayListExtra("grids", ArrayList(grids))
                putParcelableArrayListExtra("combinations", ArrayList(viewModel.getCombinations()))
                putParcelableArrayListExtra("combinationsAlreadyAdd", ArrayList(viewModel.getCombinationsBy(model)))
                startActivityForResult(this, CHANGE_GRID_CODE)
            }
        } else {
            showMessage(R.string.nenhuma_grade)
        }
    }

    private fun configureForm() {
        order_form_date.setText(calendar.toStringFormat())
        order_form_company.onItemSelected { viewModel.order.value!!.fky_empresa = viewModel.companies[it] }
        configureHourField()
        configureClientField()
        configurePaymentConditionsField()
        configureDiscountField()
        configureTotals()
    }

    private fun configureHourField() {
        with(viewModel.order.value!!) {
            if (num_codigo_online.isEmpty()) hor_emissao = calendar.time
            order_form_hour.setText(hor_emissao?.toStringFormat("HH:mm:ss"))
            order_form_hour.setOnClickListener { timeDialog.show() }
            order_form_hour.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) timeDialog.show()
            }
        }
    }

    private fun configureClientField() {
        order_form_customer.setOnClickListener { initSearchActivity() }
        order_form_customer.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) initSearchActivity()
        }
    }

    private fun configurePaymentConditionsField() {
        order_form_payment_condition.onItemSelected {
            with(viewModel) {
                if (!itemsIsEmpty()) {
                    order.value!!.fky_condicao_pagamento = conditions[it]
                    recalculateTotal()
                    configureTotals()
                    configureList()
                }
            }
        }
    }

    private fun configureDiscountField() {
        order_form_discount.addNumberMask()
        order_form_discount.afterTextChanged { text ->
            if (text.isNotEmpty()) calculateTotal(text.toDoubleValue(), true)
        }
    }

    private fun configureTotals() {
        with(viewModel.order.value!!) {
            order_form_total_item.setText(dbl_total_item.toStringValue())
            order_form_total_order.setText(dbl_total_pedido.toStringValue())
            order_form_discount.isEnabled = dbl_total_item > 0
        }
        calculateTotal(order_form_discount.toDoubleValue(), false)
    }

    private fun calculateTotal(discount: Double, discountIsTyping: Boolean) {
        with(viewModel.order.value!!) {
            val orderValue = dbl_total_item - discount
            val valueWithTwoDigits = (orderValue * 10.0).roundToLong() / 10.0
            dbl_total_pedido = valueWithTwoDigits
            if (oldDiscount > 0 && !discountIsTyping) {
                dbl_total_pedido = dbl_total_item - oldDiscount
                dbl_total_pedido = (dbl_total_pedido * 10.0).roundToLong() / 10.0
                order_form_discount.setTextSafely(oldDiscount)
            }
            order_form_total_order.setText(valueWithTwoDigits.toStringValue())
        }
    }

    private fun initSearchActivity() {
        executeAfterLoaded {
            val intent = Intent(this, PeopleSearchActivity::class.java)
            intent.putExtra("type", "C")
            startActivityForResult(intent, PEOPLE_SELECTED_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PEOPLE_SELECTED_CODE) {
            if (resultCode == Activity.RESULT_OK) data?.let {
                onCustomerSelected(it)
            }
        } else if (requestCode == MODEL_SELECTED_CODE) {
            if (resultCode == Activity.RESULT_OK) data?.extras?.let {
                onModelSelected(it)
            }
        } else if (requestCode == BUILD_GRID_CODE) {
            if (resultCode == Activity.RESULT_OK) data?.extras?.let {
                onBuildReturned(it)
            }
        } else if (requestCode == CHANGE_GRID_CODE) {
            if (resultCode == Activity.RESULT_OK) data?.extras?.let {
                onChangedGridsReturned(it)
            }
        }
        hideKeyboard()
    }

    private fun onCustomerSelected(intent: Intent) {
        val people = intent.getParcelableExtra("people_response") as Customer
        order_form_customer.setText(people.dsc_nome_pessoa)
        viewModel.order.value?.fky_cliente = people
    }

    private fun onModelSelected(bundle: Bundle) {
        val selectedModel = bundle.getParcelable<Model>("model_response")
        val conditionPosition = order_form_payment_condition.selectedItemPosition
        with(Intent(this, OrderGridFormActivity::class.java)) {
            putExtra("model", selectedModel)
            putExtra("paymentCondition", viewModel.getPaymentCondition(conditionPosition))
            putParcelableArrayListExtra("combinations", ArrayList(viewModel.getCombinations()))
            putParcelableArrayListExtra("combinationsAlreadyAdd", ArrayList(viewModel.getCombinationsBy(selectedModel)))
            startActivityForResult(this, BUILD_GRID_CODE)
        }
    }

    private fun onBuildReturned(bundle: Bundle) {
        handleBuildFormResponse(bundle) { orderItem, buildedGrids, _ ->
            with(viewModel) {
                val sequence = addItem(orderItem)
                addAllBuildedGrids(buildedGrids, sequence)
                calculateTotal()
            }
        }
    }

    private fun onChangedGridsReturned(bundle: Bundle) {
        handleBuildFormResponse(bundle) { orderItem, buildedGrids, removedGrids ->
            with(viewModel) {
                addAllRemovedGrids(removedGrids)
                updateItem(orderItem)
                updateGrids(buildedGrids.filter { it.num_codigo_online.isNotEmpty() })
                addAllBuildedGrids(buildedGrids.filter { it.num_codigo_online.isEmpty() }, orderItem.cod_sequencia!!)
                removeGrids()
                calculateTotal()
            }
        }
    }

    private fun handleBuildFormResponse(bundle: Bundle,
        execute: (orderItem: OrderItem, buildedGrids: MutableList<OrderGridItem>, removedGrids: MutableList<OrderGridItem>) -> Unit) {
        val buildedGrids = bundle.getParcelableArrayList<OrderGridItem>("grids")!!
        val removedGrids = bundle.getParcelableArrayList<OrderGridItem>("removedGrids")!!
        val orderItem = bundle.getParcelable<OrderItem>("orderItem")!!
        orderItem.dbl_quantidade = buildedGrids.fold(0.0) { sum, el -> sum + el.dbl_quantidade }

        execute(orderItem, buildedGrids, removedGrids)

        configureList()
        configureTotals()
    }

    private fun configureBtnConfirm() {
        order_form_btn_confirm.setOnClickListener {
            executeAfterLoaded {
                if (formIsValid()) {
                    showProgress()
                    val firebaseToken = FirebaseUtils.getToken(this)

                    if (viewModel.order.value!!.num_codigo_online.isEmpty())
                         insertOrder(firebaseToken)
                    else updateOrder(firebaseToken)
                }
            }
        }
    }

    override fun onClickedMenu() {
        executeAfterLoaded {
            startActivityForResult(Intent(this, ModelSearchActivity::class.java),
                MODEL_SELECTED_CODE)
        }
    }

    private fun formIsValid(): Boolean {
        return try {
            with(viewModel) {
                return if (itemsIsEmpty() && newItemsIsEmpty()) {
                    showMessage(getString(R.string.nenhum_item_adicionado))
                    false
                } else {
                    with(order.value!!) {
                        dbl_total_item = order_form_total_item.toDoubleValue()
                        dbl_desconto = order_form_discount.toDoubleValue()
                        dbl_total_pedido = order_form_total_order.toDoubleValue()
                    }
                    validateForm()
                    true
                }
            }
        } catch (ex: InvalidValueException) {
            when (ex.field) {
                "Cliente" -> order_form_customer.error = ex.message!!
                else -> showMessage(ex.message!!)
            }
            false
        }
    }

    private fun insertOrder(firebaseToken: String) {
        with(viewModel) {
            insertOrder(firebaseToken)
            insertResponse.observe(thisActivity, observerHandler({
                order.value = it
                orderUpdated()
                thisActivity.insertOrderItem()
            }, {
                handleError(it, R.string.falha_inserir_pedido)
            }))
        }
    }

    private fun insertOrderItem() {
        with(viewModel) {
            insertOrderItem()
            itemInsertResponse.observe(thisActivity, observerHandler({
                if (!itemsIsEmpty()) {
                    thisActivity.updateOrderItem()
                } else {
                    thisActivity.insertOrderGridItem()
                }
            }, {
                handleError(it, R.string.falha_inserir_item_pedido)
            }))
        }
    }

    private fun insertOrderGridItem() {
        with(viewModel) {
            insertOrderGridItem()
            gridInsertResponse.observe(thisActivity, observerHandler({
                created()
            }, {
                showMessage(it, R.string.falha_inserir_grade_pedido)
            }, {
                hideProgress()
            }))
        }
    }

    private fun updateOrder(firebaseToken: String) {
        with(viewModel) {
            updateOrder(firebaseToken)
            updateResponse.observe(thisActivity, observerHandler({
                order.value!!.version = it.version
                orderUpdated()
                removeItems()
            }, {
                handleError(it, R.string.falha_atualizar_pedido)
            }))
        }
    }

    private fun updateOrderItem() {
        with(viewModel) {
            updateOrderItem()
            itemUpdateResponse.observe(thisActivity, observerHandler({
                updateItems(it)
                thisActivity.updateOrderGridItem()
            }, {
                handleError(it, R.string.falha_atualizar_item_pedido)
            }))
        }
    }

    private fun updateOrderGridItem() {
        with(viewModel) {
            updateOrderGridItem()
            gridUpdateResponse.observe(thisActivity, observerHandler({
                if (newGridsIsEmpty()) {
                    created()
                } else {
                    thisActivity.insertOrderGridItem()
                }
            }, {
                showMessage(it, R.string.falha_atualizar_grade_pedido)
            }, {
                hideProgress()
            }))
        }
    }

    private fun removeItems() {
        with(viewModel) {
            if (removedItemsIsEmpty()) {
                filterOnlyOldGrids()
                thisActivity.removeGrids()
            } else {
                removeOrderItems()
                itemRemoveResponse.observe(thisActivity, observerHandler({
                    removeItem(0)
                    removeItems()
                }, {
                    handleError(it, R.string.falha_remover_itens_pedido)
                }))
            }
        }
    }

    private fun removeGrids() {
        with(viewModel) {
            if (removedGridsIsEmpty()) {
                if (newItemsIsEmpty()) {
                    thisActivity.updateOrderItem()
                } else {
                    thisActivity.insertOrderItem()
                }
            } else {
                removeOrderGrids()
                gridRemoveResponse.observe(thisActivity, observerHandler({
                    removeGrid(0)
                    thisActivity.removeGrids()
                }, {
                    handleError(it, R.string.falha_remover_itens_pedido)
                }))
            }
        }
    }

    private fun created() {
        with(Intent()) {
            putExtra("order_response", viewModel.order.value!!)
            setResult(Activity.RESULT_OK, this)
            finish()
        }
    }

    private fun edit() {
        try {
            showProgress()
            with(viewModel) {
                order_form_company.setSelection(getCompanyIndex())
                order_form_payment_condition.setSelection(getPaymentConditionIndex())

                with(order.value!!) {
                    order_form_date.setText(calendar.toStringFormat("dd/MM/yyyy", dat_emissao))
                    order_form_hour
                        .setText(calendar.toStringFormat("HH:mm:ss", hor_emissao ?: Date()))
                    order_form_customer.setText(fky_cliente.dsc_nome_pessoa)
                    order_form_obs.setText(dsc_observacao)

                    order_form_total_item.setText(dbl_total_item.toStringValue())
                    order_form_total_order.setText(dbl_total_pedido.toStringValue())
                }
            }
        } finally {
            Handler().postDelayed({ hideProgress() }, 250)
        }
    }

}
