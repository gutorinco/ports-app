package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Order
import br.com.suitesistemas.portsmobile.entity.OrderGridItem
import br.com.suitesistemas.portsmobile.entity.OrderItem
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.order.OrderRepository
import br.com.suitesistemas.portsmobile.service.order.gridItem.OrderGridItemRepository
import br.com.suitesistemas.portsmobile.service.order.item.OrderItemRepository
import br.com.suitesistemas.portsmobile.viewModel.completeList.ListViewModel

class OrderViewModel : ListViewModel<Order, OrderRepository>("pedido") {

    private lateinit var orderItemRepository: OrderItemRepository
    private lateinit var orderGridRepository: OrderGridItemRepository
    private val removedItems: MutableList<OrderItem> = mutableListOf()
    private val removedGrids: MutableList<OrderGridItem> = mutableListOf()
    var itemResponse = MutableLiveData<ApiResponse<MutableList<OrderItem>?>>()
    var itemRollbackResponse = MutableLiveData<ApiResponse<MutableList<OrderItem>?>>()
    var gridResponse = MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>>()
    var gridRollbackResponse = MutableLiveData<ApiResponse<MutableList<OrderGridItem>?>>()

    override fun initRepositories(company: String) {
        companyName = company
        repository = OrderRepository(company)
        orderItemRepository = OrderItemRepository(company)
        orderGridRepository = OrderGridItemRepository(company)
    }

    fun findAllItemsByOrder(position: Int) {
        val order = getBy(position)
        itemResponse = orderItemRepository.findAll(order.num_codigo_online)
    }

    fun findAllGridsByOrder(position: Int) {
        val order = getBy(position)
        gridResponse = orderGridRepository.findAll(order.num_codigo_online)
    }

    fun addRemovedItems(items: List<OrderItem>) {
        with (removedItems) {
            clear()
            addAll(items)
        }
    }

    fun addRemovedGrids(grids: List<OrderGridItem>) {
        with (removedGrids) {
            clear()
            addAll(grids)
        }
    }

    fun deleteOrder(position: Int, firebaseToken: String) {
        val order = getBy(position)

        repository.delete(order.num_codigo_online, firebaseToken, {
            removedObject = Order(order)
            removedPosition = position
            completeList.removeAt(position)
            response.value = ApiResponse(getList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })

    }

    fun deleteItemRollback(order: Order) {
        removedItems.forEach { it.fky_pedido = order }
        itemRollbackResponse = orderItemRepository.insert(removedItems)
    }

    fun deleteGridRollback(order: Order) {
        removedGrids.forEach {
            it.fky_pedido = order
            it.ids.fky_pedido = order.num_codigo_online
        }
        gridRollbackResponse = orderGridRepository.insert(removedGrids)
    }

    fun existItems() = removedItems.isNotEmpty()

    fun existGrids() = removedGrids.isNotEmpty()

    override fun sortingList(list: List<Order>) = list
        .sortedWith(compareBy(Order::dat_emissao, Order::hor_emissao))
        .asReversed()

}