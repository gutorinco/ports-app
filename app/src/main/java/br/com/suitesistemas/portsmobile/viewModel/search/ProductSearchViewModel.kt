package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Product
import br.com.suitesistemas.portsmobile.model.entity.ProductColor
import br.com.suitesistemas.portsmobile.model.enums.EConfigProductSearch
import br.com.suitesistemas.portsmobile.service.product.ProductRepository
import br.com.suitesistemas.portsmobile.service.product_color.ProductColorRepository

class ProductSearchViewModel : SelectSearchViewModel<Product>() {

    @Bindable var searchTypeText = MutableLiveData<Boolean>()
    @Bindable var isBarCode = MutableLiveData<Boolean>()
    var productColorResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    var productColorRollbackResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    val removedProductColors: MutableList<ProductColor> = mutableListOf()
    lateinit var searchBy: EConfigProductSearch
    private lateinit var productColorRepository: ProductColorRepository

    init {
        searchTypeText.value = true
        isBarCode.value = false
    }

    override fun initRepository(company: String) {
        companyName = company
        repository = ProductRepository(company)
        productColorRepository = ProductColorRepository(company)
    }

    fun fetchAllColorsBy(position: Int) {
        val product = getBy(position)
        deletedOnSearch = true
        productColorResponse = productColorRepository.findBy(product.num_codigo_online)
    }

    fun addRemovedProductColors(colors: MutableList<ProductColor>) {
        removedProductColors.clear()
        removedProductColors.addAll(colors)
    }

    fun productColorsDeleteRollback() {
        removedProductColors.forEach { it.cod_produto = removedObject!! }
        productColorRollbackResponse = productColorRepository.insert(removedProductColors)
    }

    override fun search(search: String) {
        completeList.clear()
        searching.value = true
        wasSearched.value = true
        response = (repository as ProductRepository).search(search, when (searchBy) {
            EConfigProductSearch.CODIGO -> "Código"
            EConfigProductSearch.COD_BARRAS -> "Barras"
            EConfigProductSearch.DESCRICAO -> "Nome"
            EConfigProductSearch.REFERENCIA -> "Referência"
        })
    }

    override fun sortingList(list: MutableList<Product>) = list.sortedWith(compareBy(Product::dsc_produto))

}