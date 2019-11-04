package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Configuration
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.entity.ProductColor
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.ESystemType
import br.com.suitesistemas.portsmobile.service.configuration.ConfigurationRepository
import br.com.suitesistemas.portsmobile.service.product.ProductRepository
import br.com.suitesistemas.portsmobile.service.product_color.ProductColorRepository
import br.com.suitesistemas.portsmobile.viewModel.completeList.ListViewModel

class ProductViewModel : ListViewModel<Product, ProductRepository>("produto") {

    private lateinit var productColorRepository: ProductColorRepository
    private lateinit var configRepository: ConfigurationRepository
    var systemType = ESystemType.A
    var configResponse = MutableLiveData<ApiResponse<MutableList<Configuration>?>>()
    var productColorResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    var productColorRollbackResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    val removedProductColors: MutableList<ProductColor> = mutableListOf()

    override fun initRepositories(company: String) {
        companyName = company
        repository = ProductRepository(company)
        configRepository = ConfigurationRepository(company)
        productColorRepository = ProductColorRepository(company)
    }

    fun addRemovedProductColors(colors: MutableList<ProductColor>) {
        removedProductColors.clear()
        removedProductColors.addAll(colors)
    }

    fun fetchAllColorsBy(position: Int) {
        val product = getBy(position)
        productColorResponse = productColorRepository.findBy(product.num_codigo_online)
    }

    fun fetchConfigurations(){
        configResponse = configRepository.findAll()
    }

    fun productColorsDeleteRollback() {
        removedProductColors.forEach { it.cod_produto = removedObject!! }
        productColorRollbackResponse = productColorRepository.insert(removedProductColors)
    }

    override fun sortingList(list: List<Product>) = list.sortedWith(compareBy(Product::dsc_produto))

}