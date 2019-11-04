package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.entity.*
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import br.com.suitesistemas.portsmobile.service.combination.CombinationRepository
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository
import br.com.suitesistemas.portsmobile.service.order.OrderRepository
import br.com.suitesistemas.portsmobile.service.order.gridItem.OrderGridItemRepository
import br.com.suitesistemas.portsmobile.service.order.item.OrderItemRepository
import br.com.suitesistemas.portsmobile.service.payment_condition.PaymentConditionRepository
import br.com.suitesistemas.portsmobile.service.user.UserRepository
import java.util.*
import kotlin.math.roundToLong

class OrderFormViewModel(application: Application) : FormViewModel<Order>(application) {

    @Bindable var order = MutableLiveData<Order>()
    val conditions: MutableList<PaymentCondition> = mutableListOf()
    private val combinations: MutableList<Combination> = mutableListOf()
    private val items: MutableList<OrderItem> = mutableListOf()
    private var grids: MutableList<OrderGridItem> = mutableListOf()
    private val newItems: MutableList<OrderItem> = mutableListOf()
    private var newGrids: MutableList<OrderGridItem> = mutableListOf()
    private val removedItems: MutableList<OrderItem> = mutableListOf()
    private var removedGrids: MutableList<OrderGridItem> = mutableListOf()
    private lateinit var userRepository: UserRepository
    private lateinit var repository: OrderRepository
    private lateinit var itemRepository: OrderItemRepository
    private lateinit var gridRepository: OrderGridItemRepository
    private lateinit var combinationRepository: CombinationRepository
    private lateinit var paymentConditionRepository: PaymentConditionRepository
    private var userId: Int? = null
    var userResponse = MutableLiveData<ApiResponse<User?>>()
    var itemResponse = MutableLiveData<ApiResponse<MutableList<OrderItem>?>>()
    var itemInsertResponse = MutableLiveData<ApiResponse<MutableList<OrderItem>?>>()
    var itemUpdateResponse = MutableLiveData<ApiResponse<MutableList<OrderItem>?>>()
    var itemRemoveResponse = MutableLiveData<ApiResponse<Boolean?>>()
    var gridResponse = MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>>()
    var gridInsertResponse = MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>>()
    var gridUpdateResponse = MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>>()
    var gridRemoveResponse = MutableLiveData<ApiResponse<Boolean?>>()
    var combinationResponse = MutableLiveData<ApiResponse<MutableList<Combination>?>>()
    var paymentConditionResponse = MutableLiveData<ApiResponse<MutableList<PaymentCondition>?>>()

    init {
        order.value = Order()
    }

    fun initRepositories(companyName: String, userId: Int) {
        userRepository = UserRepository(companyName)
        repository = OrderRepository(companyName)
        itemRepository = OrderItemRepository(companyName)
        gridRepository = OrderGridItemRepository(companyName)
        companyRepository = CompanyRepository(companyName)
        combinationRepository = CombinationRepository(companyName)
        paymentConditionRepository = PaymentConditionRepository(companyName)
        this.userId = userId
    }

// USER

    fun findLoggedUser() {
        userResponse = userRepository.find(userId!!)
    }

// COMPANY

    override fun getCompanyIndex(): Int {
        val company = companies.find { it.cod_empresa == order.value?.fky_empresa?.cod_empresa }
        company?.let {
            val companyIndex = companies.indexOf(it)
            return if (companyIndex >= 0) companyIndex - 1 else 0
        }
        return 0
    }

// PAYMENT CONDITION

    fun fetchAllPaymentConditions() {
        paymentConditionResponse = when (conditions.isNullOrEmpty()) {
            true -> paymentConditionRepository.findAll()
            false -> getApiResponseFromExistList(conditions)
        }
    }

    fun getPaymentConditionsNames() = conditions.map { condition -> condition.dsc_condicao_pagamento }

    fun getPaymentConditionIndex(): Int = conditions.indexOfFirst { condition ->
        val conditionId = order.value?.fky_condicao_pagamento?.cod_condicao_pagamento!!
        if (conditionId > 0)
            condition.cod_condicao_pagamento = conditionId
        condition.flg_a_vista == EYesNo.S
    }

    fun getPaymentCondition(position: Int) = conditions[position]

    fun addAllPaymentConditions(conditions: MutableList<PaymentCondition>) {
        this.conditions.addAll(conditions)
    }

// COMBINATIONS

    fun fetchAllCombinations() {
        combinationResponse = combinationRepository.findAll()
    }

    fun getCombinations() : MutableList<Combination> = combinations

    fun addAllCombinations(list: MutableList<Combination>) {
        combinations.clear()
        combinations.addAll(list)
    }

    fun getCombinationsBy(model: Model): List<Combination> {
        val combinations = mutableListOf<Combination>()
        combinations.addAll(items
            .filter { it.fky_modelo.num_codigo_online == model.num_codigo_online }
            .map { it.fky_combinacao })
        combinations.addAll(newItems
            .filter { it.fky_modelo.num_codigo_online == model.num_codigo_online }
            .map { it.fky_combinacao })
        return combinations
    }

// ITEMS

    fun findItemsBy(order: Order) {
        items.clear()
        itemResponse = itemRepository.findAll(order.num_codigo_online)
    }

    fun itemsIsEmpty() : Boolean = items.isEmpty()

    fun newItemsIsEmpty() : Boolean = newItems.isEmpty()

    fun removedItemsIsEmpty() : Boolean = removedItems.isEmpty()

    fun getItems() : MutableList<OrderItem> {
        val items: MutableList<OrderItem> = mutableListOf()
        items.addAll(newItems)
        items.addAll(this.items)
        return items
    }

    fun addItem(item: OrderItem): Int {
        item.cod_sequencia = getSequence()
        newItems.add(item)
        return item.cod_sequencia!!
    }

    fun addAllItems(items: List<OrderItem>) {
        this.items.addAll(items)
    }

    fun removeItem(index: Int) {
        removedItems.removeAt(index)
    }

    fun removeItemBy(position: Int) {
        val item = try {
            items[position]
        } catch (ex: IndexOutOfBoundsException) {
            newItems[position]
        }

        removedItems.add(OrderItem(item))
        items.remove(item)
        newItems.remove(item)
        removedGrids = (removedGrids.filter { it.ids.cod_sequencia != item.cod_sequencia }).toMutableList()
        grids = (grids.filter { it.ids.cod_sequencia != item.cod_sequencia }).toMutableList()
    }

// GRIDS

    fun findGridsBy(order: Order) {
        grids.clear()
        gridResponse = gridRepository.findAll(order.num_codigo_online)
    }

    fun addAllGrids(orderGrids: List<OrderGridItem>) {
        grids.addAll(orderGrids)
    }

    fun removeGrid(index: Int) {
        removedGrids.removeAt(index)
    }

    fun newGridsIsEmpty() : Boolean = newGrids.isEmpty()

    fun removedGridsIsEmpty() : Boolean = removedGrids.isEmpty()

    fun addAllRemovedGrids(orderGrids: List<OrderGridItem>) {
        removedGrids.addAll(orderGrids)
    }

    fun getGrids() : MutableList<OrderGridItem> {
        val grids: MutableList<OrderGridItem> = mutableListOf()
        grids.addAll(newGrids)
        grids.addAll(this.grids)
        return grids
    }

    fun getGridsBy(sequence: Int): List<OrderGridItem> {
        val grids = grids.filter { it.ids.cod_sequencia == sequence }
        if (grids.isEmpty())
            return newGrids.filter { it.ids.cod_sequencia == sequence }
        return grids
    }

    fun getTotalItems(): Double {
        val total = items.fold(0.0) { sum, el -> sum + el.dbl_total_item }
        return total + newItems.fold(0.0) { sum, el -> sum + el.dbl_total_item }
    }

// CALCULATION

    fun calculateTotal() {
        order.value?.dbl_total_item = getTotalItems()
    }

    fun recalculateTotal() {
        val paymentCondition = order.value!!.fky_condicao_pagamento
        items.forEach { item ->
            val grids = this.grids.filter { it.ids.cod_sequencia == item.cod_sequencia }
            with (grids) {
                val model = item.fky_modelo
                item.dbl_preco_unit = when {
                    paymentCondition.flg_a_vista == EYesNo.S -> model.dbl_preco_unit_vista
                    paymentCondition.flg_a_vista == EYesNo.N -> model.dbl_preco_unit_prazo
                    else -> 0.0
                }
                val totalItems = fold(0.0) { sum, el -> sum + getTotalItem(item, el) }
                val subtotal = totalItems - (item.dbl_desconto ?: 0.0)
                item.dbl_total_item = (subtotal * 10.0).roundToLong() / 10.0
            }
        }
    }

    private fun getTotalItem(orderItem: OrderItem, orderGrid: OrderGridItem) : Double {
        val value = orderItem.dbl_preco_unit * orderGrid.dbl_quantidade
        return value * orderGrid.int_caixa!!
    }

// LISTS MANIPULATIONS

    fun addAllBuildedGrids(buildedGrids: List<OrderGridItem>, sequence: Int) {
        buildedGrids.forEach {
            it.ids.cod_sequencia = sequence
            it.ids.fky_pedido = order.value!!.num_codigo_online
            it.num_codigo_online = UUID.randomUUID().toString()
        }
        newGrids.addAll(buildedGrids)
    }

    private fun getSequence(): Int {
        if (items.isEmpty() && newItems.isEmpty())
            return 1

        val itemsSequence = getItemsSequence(items)
        val newItemsSequence = getItemsSequence(newItems)

        if (itemsSequence > newItemsSequence)
            return itemsSequence + 1
        return newItemsSequence + 1
    }

    private fun getItemsSequence(items: MutableList<OrderItem>): Int {
        return items.fold(0) { sum, element ->
            when {
                element.cod_sequencia == null -> 1
                sum > element.cod_sequencia!! -> sum
                else -> element.cod_sequencia!!
            }
        }
    }

    fun filterOnlyOldGrids() {
        removedGrids = (removedGrids.filter { it.num_codigo_online.isNotEmpty() }).toMutableList()
    }

    fun orderUpdated() {
        updateItemsAndGrids()
        updateNewItemsAndGrids()
    }

    private fun updateItemsAndGrids() {
        items.forEach { item ->
            item.fky_pedido = order.value!!
            item.fky_empresa = order.value!!.fky_empresa
            item.flg_tipo_desconto = "V"
        }
        grids.forEach { grid ->
            grid.fky_pedido = order.value!!
            grid.ids.fky_pedido = grid.fky_pedido.num_codigo_online
        }
    }

    private fun updateNewItemsAndGrids() {
        newItems.forEach { item ->
            item.fky_pedido = order.value!!
            item.fky_empresa = order.value!!.fky_empresa
            item.flg_tipo_desconto = "V"
            item.version = 0
        }
        newGrids.forEach { grid ->
            grid.fky_pedido = order.value!!
            grid.ids.fky_pedido = grid.fky_pedido.num_codigo_online
            grid.version = 0
        }
    }

    fun updateItem(orderItem: OrderItem) {
        val item = items.find { it.cod_sequencia == orderItem.cod_sequencia }
        item?.copy(orderItem)

        val newItem = newItems.find { it.cod_sequencia == orderItem.cod_sequencia }
        newItem?.copy(orderItem)
    }

    fun updateItems(changedItems: MutableList<OrderItem>) {
        changedItems.forEach { changedItem ->
            for (item in items) {
                if (item.cod_sequencia == changedItem.cod_sequencia) {
                    item.copy(changedItem)
                    break
                }
            }
        }
    }

    fun updateGrids(changedGrids: List<OrderGridItem>) {
        changedGrids.forEach { changedGrid ->
            val grid = grids.find { it.ids == changedGrid.ids }
            grid?.copy(changedGrid)
        }
    }

    fun removeGrids() {
        removedGrids.forEach { removed ->
            grids = (grids.filter { it.ids != removed.ids }).toMutableList()
            newGrids = (newGrids.filter { it.ids != removed.ids }).toMutableList()
        }
    }

// FORM

    fun validateForm() {
        val order = Order(this.order.value!!)

        if (conditions.isNullOrEmpty())
            throw InvalidValueException(getStringRes(R.string.nenhuma_condicao))
        if (companies.isEmpty())
            throw InvalidValueException(getStringRes(R.string.nenhuma_empresa))
        if (order.fky_cliente.dsc_nome_pessoa.isEmpty())
            throw InvalidValueException("Cliente", getStringRes(R.string.obrigatorio))

        if (order.num_codigo_online.isEmpty()) {
            order.dat_emissao = Date()
            order.hor_emissao = Date()
        }

        this.order.value = Order(order)
    }

// REPOSITORY FUNCTIONS

    fun insertOrder(firebaseToken: String) {
        insertResponse = repository.insert(getJsonRequest("pedido", order.value!!, firebaseToken))
    }

    fun insertOrderItem() {
        itemInsertResponse = itemRepository.insert(newItems)
    }

    fun insertOrderGridItem() {
        newGrids.forEach { grid -> grid.fky_pedido = order.value!! }
        gridInsertResponse = gridRepository.insert(newGrids)
    }

    fun updateOrder(firebaseToken: String) {
        updateResponse = repository.update(getJsonRequest("pedido", order.value!!, firebaseToken))
    }

    fun updateOrderItem() {
        itemUpdateResponse = itemRepository.update(items)
    }

    fun updateOrderGridItem() {
        gridUpdateResponse = gridRepository.update(grids)
    }

    fun removeOrderItems() {
        val orderItem = removedItems.first()
        itemRemoveResponse = itemRepository.delete(orderItem.num_codigo_online, orderItem.cod_sequencia!!)
    }

    fun removeOrderGrids() {
        val orderGrid = removedGrids.first()
        gridRemoveResponse = gridRepository.delete(orderGrid.num_codigo_online)
    }

}