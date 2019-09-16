package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.entity.SaleItem
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.sale.SaleRepository
import br.com.suitesistemas.portsmobile.service.sale.item.SaleItemRepository

class SaleSearchViewModel : SearchViewModel<Sale>() {

    var isGetMethod: Boolean = false
    var itemResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
    var saleRollbackResponse = MutableLiveData<ApiResponse<Sale?>>()
    var itemRollbackResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
    private lateinit var saleRepository: SaleRepository
    private lateinit var saleItemRepository: SaleItemRepository
    private val sales: MutableList<Sale> = mutableListOf()
    private val items: MutableList<SaleItem> = mutableListOf()

    fun initRepository(companyName: String, isGetMethod: Boolean) {
        this.companyName = companyName
        this.isGetMethod = isGetMethod
        saleRepository = SaleRepository(companyName)
        saleItemRepository = SaleItemRepository(companyName)
    }

    override fun addAll(list: MutableList<Sale>) {
        sales.addAll(list)
        listIsEmpty.value = sales.isNullOrEmpty()
    }

    fun add(sale: Sale, operation: EHttpOperation = EHttpOperation.POST) {
        if (operation == EHttpOperation.ROLLBACK)
            sales.add(removedPosition, sale)
        else sales.add(sale)
        response.value = ApiResponse(getList(), operation)
    }

    override fun search(search: String) {
        sales.clear()
        searching.value = true
        wasSearched.value = true
        response = saleRepository.search(search)
    }

    override fun getList(): MutableList<Sale> = getListCopy(sortingList())
    fun existItems() = items.isNotEmpty()
    fun getSaleBy(position: Int) = sales[position]

    fun findAllItemsBySale(position: Int) {
        val sale = getSaleBy(position)
        itemResponse = saleItemRepository.findAll(sale.num_codigo_online)
    }

    fun deleteSale(position: Int, itemsBySale: List<SaleItem>, firebaseToken: String) {
        val sale = getSaleBy(position)
        items.clear()
        items.addAll(itemsBySale)

        saleRepository.delete(sale.num_codigo_online, firebaseToken, {
            removedObject = sale
            removedPosition = position
            sales.removeAt(position)
            response.value = ApiResponse(getList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })

    }

    fun deleteRollback() {
        saleRollbackResponse = saleRepository.insert(getJsonRequest("venda"))
    }

    fun deleteItemRollback(sale: Sale) {
        sales.add(sale)
        items.forEach { it.num_codigo_online = sale.num_codigo_online }
        itemRollbackResponse = saleItemRepository.insert(items)
    }

    private fun sortingList(): MutableList<Sale> {
        val sortedSales = sales
            .sortedWith(compareBy(Sale::dat_emissao, Sale::hor_emissao))
            .asReversed()
        sales.clear()
        sales.addAll(sortedSales)
        return sales
    }

}