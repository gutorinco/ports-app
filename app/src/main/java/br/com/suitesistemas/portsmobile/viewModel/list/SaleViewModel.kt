package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.entity.SaleItem
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.sale.SaleRepository
import br.com.suitesistemas.portsmobile.service.sale.item.SaleItemRepository

class SaleViewModel : ListViewModel<Sale>() {

    private lateinit var saleRepository: SaleRepository
    private lateinit var saleItemRepository: SaleItemRepository
    private val items: MutableList<SaleItem> = mutableListOf()
    var itemResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
    var itemRollbackResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()

    fun fetchAllSales(companyName: String) {
        this.companyName = companyName
        saleRepository = SaleRepository(companyName)
        saleItemRepository = SaleItemRepository(companyName)
        response = saleRepository.findAll()
    }

    fun findAllItemsBySale(position: Int) {
        val sale = getBy(position)
        itemResponse = saleItemRepository.findAll(sale.num_codigo_online)
    }

    fun refresh() {
        list.clear()
        refreshResponse = saleRepository.findAll()
    }

    fun updateList(saleResponse: Sale) {
        for (sale in list)
            if (sale.num_codigo_online == saleResponse.num_codigo_online)
                sale.copy(saleResponse)
        response.value = ApiResponse(getSortingList(), EHttpOperation.PUT)
    }

    fun deleteSale(position: Int, saleItems: List<SaleItem>, firebaseToken: String) {
        items.clear()
        if (!saleItems.isNullOrEmpty())
            items.addAll(saleItems)

        val sale = getBy(position)

        saleRepository.delete(sale.num_codigo_online, firebaseToken, {
            removedObject = Sale(sale)
            removedPosition = position
            list.removeAt(position)
            response.value = ApiResponse(getSortingList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })

    }

    fun deleteRollback() {
        rollbackResponse = saleRepository.insert(getJsonRequest("venda"))
    }

    fun deleteItemRollback(sale: Sale) {
        list.add(sale)
        items.forEach { it.num_codigo_online = sale.num_codigo_online }
        itemRollbackResponse = saleItemRepository.insert(items)
    }

    fun existItems() = items.isNotEmpty()
    override fun sortingList() = list
            .sortedWith(compareBy(Sale::dat_emissao, Sale::hor_emissao))
            .asReversed()

}