package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.entity.SaleItem
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.sale.SaleRepository
import br.com.suitesistemas.portsmobile.service.sale.item.SaleItemRepository
import br.com.suitesistemas.portsmobile.viewModel.completeList.ListViewModel

class SaleViewModel : ListViewModel<Sale, SaleRepository>("venda") {

    private lateinit var saleItemRepository: SaleItemRepository
    private val items: MutableList<SaleItem> = mutableListOf()
    var itemResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
    var itemRollbackResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()

    override fun initRepositories(company: String) {
        companyName = company
        repository = SaleRepository(company)
        saleItemRepository = SaleItemRepository(company)
    }

    fun findAllItemsBySale(position: Int) {
        val sale = getBy(position)
        itemResponse = saleItemRepository.findAll(sale.num_codigo_online)
    }

    fun deleteSale(position: Int, saleItems: List<SaleItem>, firebaseToken: String) {
        items.clear()
        if (!saleItems.isNullOrEmpty())
            items.addAll(saleItems)

        val sale = getBy(position)

        repository.delete(sale.num_codigo_online, firebaseToken, {
            removedObject = Sale(sale)
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

    fun existItems() = items.isNotEmpty()

    override fun sortingList(list: List<Sale>) = list
            .sortedWith(compareBy(Sale::dat_emissao, Sale::hor_emissao))
            .asReversed()

}