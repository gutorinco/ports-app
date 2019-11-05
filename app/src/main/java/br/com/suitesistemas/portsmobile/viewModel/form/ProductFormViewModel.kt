package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.CodeResponse
import br.com.suitesistemas.portsmobile.model.entity.*
import br.com.suitesistemas.portsmobile.model.enums.ESystemType
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository
import br.com.suitesistemas.portsmobile.service.configuration.ConfigurationRepository
import br.com.suitesistemas.portsmobile.service.product.ProductRepository
import br.com.suitesistemas.portsmobile.service.product_color.ProductColorRepository
import br.com.suitesistemas.portsmobile.service.unit_measure.UnitMeasureRepository

class ProductFormViewModel(application: Application) : FormViewModel<Product>(application) {

    @Bindable var product = MutableLiveData<Product>()
    @Bindable var code = MutableLiveData<String>()
    val units: MutableList<UnitMeasure> = mutableListOf()
    val colors: MutableList<Color> = mutableListOf()
    var removedColor: Color? = null
    var systemType = ESystemType.A
    val removedColors: MutableList<Color> = mutableListOf()
    val productColors: MutableList<ProductColor> = mutableListOf()
    val newProductColors: MutableList<ProductColor> = mutableListOf()
    var codeResponse = MutableLiveData<ApiResponse<CodeResponse?>>()
    var unitsResponse = MutableLiveData<ApiResponse<MutableList<UnitMeasure>?>>()
    var productColorResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    var productColorInsertResponse = MutableLiveData<ApiResponse<MutableList<ProductColor>?>>()
    var productColorDeleteResponse = MutableLiveData<ApiResponse<Boolean?>>()
    var configResponse = MutableLiveData<ApiResponse<MutableList<Configuration>?>>()
    private lateinit var configRepository: ConfigurationRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var productColorRepository: ProductColorRepository
    private lateinit var unitMeasureRepository: UnitMeasureRepository

    init {
        product.value = Product()
        code.value = ""
    }

    fun initRepositories(companyName: String) {
        productRepository = ProductRepository(companyName)
        companyRepository = CompanyRepository(companyName)
        configRepository = ConfigurationRepository(companyName)
        unitMeasureRepository = UnitMeasureRepository(companyName)
        productColorRepository = ProductColorRepository(companyName)
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

    fun fetchConfigurations(){
        configResponse = configRepository.findAll()
    }

    fun fetchNextCode() {
        codeResponse = productRepository.getNextCode()
    }

    override fun getCompanyIndex() = companies.indexOfFirst { company ->
        company.cod_empresa == product.value?.fky_empresa?.cod_empresa
    }

    fun getUnitNames() = units.map { unit -> unit.dsc_unidade_medida }

    fun getUnitIndex() = units.indexOfFirst { company ->
        company.cod_unidade_medida == product.value?.fky_unidade_medida?.cod_unidade_medida
    }

    fun addAllUnits(units: MutableList<UnitMeasure>) {
        this.units.addAll(units)
    }

    fun addAllProductColors(productColors: MutableList<ProductColor>) {
        this.productColors.addAll(productColors)
        colors.addAll(productColors.map { it.cod_cor })
    }

    fun listIsEmpty() = colors.isNullOrEmpty()

    fun concat(productToConcat: Product) {
        product.value = productToConcat
        code.value = productToConcat.cod_online.toString()
    }

    fun validateForm() {
        val product = Product(this.product.value!!)

        if (units.isNullOrEmpty())
            throw InvalidValueException(getStringRes(R.string.nenhuma_unidade_medida))
        if (colors.isEmpty())
            throw InvalidValueException(getStringRes(R.string.adicione_cores))
        if (product.dsc_produto.isNullOrEmpty())
            throw InvalidValueException("Nome", getStringRes(R.string.obrigatorio))

        this.product.value = Product(product)
    }

    fun insert(firebaseToken: String) {
        insertResponse = productRepository.insert(getJsonRequest("produto", product.value!!, firebaseToken))
    }

    fun update(firebaseToken: String) {
        updateResponse = productRepository.update(getJsonRequest("produto", product.value!!, firebaseToken))
    }

    fun insertColors() {
        newProductColors.forEach {
            it.cod_produto = product.value!!
            if (systemType == ESystemType.O)
                it.dsc_codigo_barras = getBarCode(it.cod_produto, it.cod_cor)
        }
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

    private fun getBarCode(product: Product, color: Color): String {
        val productCode = product.cod_online
        val textProductCode = when {
            productCode < 10 -> "00000$productCode"
            productCode < 100 -> "0000$productCode"
            productCode < 1000 -> "000$productCode"
            productCode < 10000 -> "00$productCode"
            productCode < 100000 -> "0$productCode"
            else -> productCode.toString()
        }

        val colorCode = color.cod_cor!!
        val textColorCode = when {
            colorCode < 10 -> "00$colorCode"
            colorCode < 100 -> "0$colorCode"
            else -> colorCode.toString()
        }

        return "$textProductCode$textColorCode"
    }

}