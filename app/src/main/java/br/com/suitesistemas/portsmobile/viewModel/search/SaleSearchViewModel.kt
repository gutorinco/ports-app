package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Customer
import br.com.suitesistemas.portsmobile.model.entity.Sale
import br.com.suitesistemas.portsmobile.model.entity.SaleItem
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.sale.SaleRepository
import br.com.suitesistemas.portsmobile.service.sale.item.SaleItemRepository

class SaleSearchViewModel : SearchViewModel<Sale>() {

    var isGetMethod: Boolean = false
    var itemResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
    var itemRollbackResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
    private lateinit var saleItemRepository: SaleItemRepository
    private val items: MutableList<SaleItem> = mutableListOf()
    var onlyCurrentUser: Boolean = false
    var currentUser = Customer()

    override fun initRepository(company: String) {
        TODO("Not implemented!")
    }

    fun initRepository(companyName: String, isGetMethod: Boolean) {
        this.companyName = companyName
        this.isGetMethod = isGetMethod
        repository = SaleRepository(companyName)
        saleItemRepository = SaleItemRepository(companyName)
    }

    override fun search(search: String) {
        completeList.clear()
        searching.value = true
        wasSearched.value = true
        response = if (onlyCurrentUser)
            (repository as SaleRepository).searchBy(currentUser.num_codigo_online, search)
        else repository.search(search)
    }

    fun existItems() = items.isNotEmpty()

    fun findAllItemsBySale(position: Int) {
        val sale = getBy(position)
        itemResponse = saleItemRepository.findAll(sale.num_codigo_online)
    }

    fun deleteSale(position: Int, itemsBySale: List<SaleItem>, firebaseToken: String) {
        val sale = getBy(position)
        items.clear()
        items.addAll(itemsBySale)
        deletedOnSearch = true

        repository.delete(sale.num_codigo_online, firebaseToken, {
            removedObject = sale
            removedPosition = position
            completeList.removeAt(position)
            response.value = ApiResponse(completeList, EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })

    }

    fun deleteItemRollback(sale: Sale) {
        items.forEach { it.num_codigo_online = sale.num_codigo_online }
        itemRollbackResponse = saleItemRepository.insert(items)
    }

    override fun sortingList(list: MutableList<Sale>) = list
        .sortedWith(compareBy(Sale::dat_emissao, Sale::hor_emissao))
        .asReversed()

}