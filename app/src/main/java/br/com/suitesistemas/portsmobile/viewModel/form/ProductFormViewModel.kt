package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.entity.ProductColor
import br.com.suitesistemas.portsmobile.entity.UnitMeasure
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository
import br.com.suitesistemas.portsmobile.service.product.ProductRepository
import br.com.suitesistemas.portsmobile.service.product_color.ProductColorRepository
import br.com.suitesistemas.portsmobile.service.unit_measure.UnitMeasureRepository

class ProductFormViewModel(application: Application) : FormViewModel<Product>(application) {

    @Bindable var product = MutableLiveData<Product>()
    val units: MutableList<UnitMeasure> = mutableListOf()
    val colors: MutableList<Color> = mutableListOf()
    var removedColor: Color? = null
    val removedColors: MutableList<Color> = mutableListOf()
    val productColors: MutableList<ProductColor> = mutableListOf()
    val newProductColors: MutableList<ProductColor> = mutableListOf()
    var unitsResponse = MutableLiveData<ApiResponse<MutableList<UnitMeasure>?>>()
    var productColorResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    var productColorInsertResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    var productColorDeleteResponse = MutableLiveData<ApiResponse<Boolean?>>()
    private lateinit var productRepository: ProductRepository
    private lateinit var productColorRepository: ProductColorRepository
    private lateinit var unitMeasureRepository: UnitMeasureRepository

    init {
        product.value = Product()
    }

    fun initRepositories(companyName: String) {
        productRepository = ProductRepository(companyName)
        companyRepository  = CompanyRepository(companyName)
        unitMeasureRepository  = UnitMeasureRepository(companyName)
        productColorRepository  = ProductColorRepository(companyName)
    }

    fun fetchAllUnits() {
        unitsResponse = when (units.isNullOrEmpty()) {
            true -> unitMeasureRepository.findAll()
            false -> getApiResponseFromExistList(units)
        }
    }

    fun fetchAllColors() {
        productColorResponse = when (productColors.isNullOrEmpty()) {
            true -> productColorRepository.findBy(product.value?.num_codigo_online!!)
            false -> getApiResponseFromExistList(productColors)
        }
    }

    fun addAllUnits(units: MutableList<UnitMeasure>) {
        this.units.addAll(units)
    }

    fun addAllProductColors(productColors: MutableList<ProductColor>) {
        this.productColors.addAll(productColors)
        colors.addAll(productColors.map { it.cod_cor })
    }

    fun concat(productToConcat: Product) {
        product.value = productToConcat
    }

    fun validateForm(unitPosition: Int, companyPosition: Int) {
        val product = Product(this.product.value!!)

        if (units.isNullOrEmpty())
            throw InvalidValueException(getStringRes(R.string.nenhuma_unidade_medida))
        if (colors.isEmpty())
            throw InvalidValueException(getStringRes(R.string.adicione_cores))

        product.fky_empresa = companies[companyPosition]
        product.fky_unidade_medida = units[unitPosition]

        if (product.dsc_produto.isNullOrEmpty())
            throw InvalidValueException("Nome", getStringRes(R.string.obrigatorio))

        this.product.value = Product(product)
    }

    fun save(firebaseToken: String) {
        if (product.value?.num_codigo_online.isNullOrEmpty())
            insert(firebaseToken)
        else update(firebaseToken)
    }

    private fun insert(firebaseToken: String) {
        insertResponse = productRepository.insert(getJsonRequest("produto", product.value!!, firebaseToken))
    }

    private fun update(firebaseToken: String) {
        updateResponse = productRepository.update(getJsonRequest("produto", product.value!!, firebaseToken))
    }

    fun insertColors() {
        newProductColors.forEach { it.cod_produto = product.value!! }
        productColorInsertResponse = productColorRepository.insert(newProductColors)
    }

    fun deleteColors() {
        removedColor = removedColors.first()
        productColorDeleteResponse = productColorRepository.delete(removedColor!!.num_codigo_online, product.value?.num_codigo_online!!)
    }

    fun removeColorBy(position: Int) {
        val color = colors[position]
        removedColors.add(color)
        colors.remove(color)
    }

    fun addSelectedColors(colorsSelected: List<Color>?) {
        colorsSelected?.map {
            if (colors.find { c -> c.num_codigo_online == it.num_codigo_online } == null) {
                newProductColors.add(ProductColor(product.value!!, it))
                colors.add(it)
            }
        }
    }

}