package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.entity.*
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.ProductDetail
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository
import br.com.suitesistemas.portsmobile.service.payment_condition.PaymentConditionRepository
import br.com.suitesistemas.portsmobile.service.product.color.ProductColorRepository
import br.com.suitesistemas.portsmobile.service.sale.SaleRepository
import br.com.suitesistemas.portsmobile.service.sale.item.SaleItemRepository
import br.com.suitesistemas.portsmobile.service.user.UserRepository
import java.util.*

class SaleFormViewModel(application: Application) : FormViewModel<Sale>(application) {

    @Bindable val sale = MutableLiveData<Sale>()
    private val removedProducts: LinkedHashMap<Product, ProductDetail> = linkedMapOf()
    private val items: MutableList<SaleItem> = mutableListOf()
    private val removedItems: MutableList<SaleItem> = mutableListOf()
    private val productsColors: MutableList<ProductColor> = mutableListOf()
    private val productsSelected: MutableList<Product> = mutableListOf()
    private val conditions: MutableList<PaymentConditions> = mutableListOf()
    private var userId: Int? = null
    private lateinit var userRepository: UserRepository
    private lateinit var saleRepository: SaleRepository
    private lateinit var saleItemRepository: SaleItemRepository
    private lateinit var companyRepository: CompanyRepository
    private lateinit var paymentConditionRepository: PaymentConditionRepository
    private lateinit var productColorRepository: ProductColorRepository
    var user: User = User()
    val products: LinkedHashMap<Product, ProductDetail> = linkedMapOf()
    var userResponse = MutableLiveData<ApiResponse<User?>>()
    var itemResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
    var itemInsertResponse = MutableLiveData<ApiResponse<MutableList<SaleItem>?>>()
    var itemUpdateResponse = MutableLiveData<ApiResponse<Any?>>()
    var itemDeleteResponse = MutableLiveData<ApiResponse<Any?>>()
    var paymentConditionResponse = MutableLiveData<ApiResponse<MutableList<PaymentConditions>?>>()

    init {
        sale.value = Sale()
    }

    fun initRepositories(companyName: String, userId: Int) {
        userRepository = UserRepository(companyName)
        saleRepository = SaleRepository(companyName)
        saleItemRepository = SaleItemRepository(companyName)
        companyRepository = CompanyRepository(companyName)
        paymentConditionRepository = PaymentConditionRepository(companyName)
        productColorRepository = ProductColorRepository(companyName)
        this.userId = userId
    }

    fun fetchAllCompanies() {
        companiesResponse = when (companies.isNullOrEmpty()) {
            true -> companyRepository.findAll()
            false -> getApiResponseFromExistList(companies)
        }
    }

    fun findLoggedUser() {
        userResponse = userRepository.find(userId!!)
    }

    fun getCompanyIndexBySale(): Int {
        val company = companies.find { it.cod_empresa == sale.value?.fky_empresa?.cod_empresa }
        company?.let {
            val companyIndex = companies.indexOf(it)
            return if (companyIndex >= 0) companyIndex else 0
        }
        return 0
    }

    fun fetchAllPaymentConditions() {
        paymentConditionResponse = when (conditions.isNullOrEmpty()) {
            true -> paymentConditionRepository.findAll()
            false -> getApiResponseFromExistList(conditions)
        }
    }

    fun getPaymentConditionIndexBySale(): Int {
        val paymentCondition = conditions.find {
            it.cod_condicao_pagamento == sale.value?.fky_condicao_pagamento?.cod_condicao_pagamento
        }
        paymentCondition?.let {
            val conditionIndex = conditions.indexOf(it)
            return if (conditionIndex >= 0) conditionIndex else 0
        }
        return 0
    }

    fun addAllPaymentConditions(conditions: MutableList<PaymentConditions>) {
        this.conditions.addAll(conditions)
    }

    fun allAllItems(items: List<SaleItem>) {
        this.items.addAll(items)
    }

    fun removeProductBy(position: Int) {
        val product = ArrayList(products.keys)[position]
        removedProducts[product] = products[product]!!
        products.remove(product)
    }

    fun itemsIsFilled() = items.isNotEmpty()
    fun selectedProducts() = productsSelected.isNotEmpty()
    fun removedItemsIsFilled() = removedItems.isNotEmpty()
    fun clearRemovedItem() = removedItems.clear()
    fun clearSelectedProducts() = productsSelected.clear()

    fun validateForm(conditionPosition: Int, companyPosition: Int) {
        val sale = Sale()
        sale.copy(this.sale.value!!)

        if (sale.fky_cliente.dsc_nome_pessoa.isEmpty()) {
            throw InvalidValueException("Cliente", getStringRes(R.string.obrigatorio))
        } else if (sale.fky_vendedor.dsc_nome_pessoa.isEmpty()) {
            throw InvalidValueException("Vendedor", getStringRes(R.string.obrigatorio))
        } else if (conditions.isNullOrEmpty()) {
            throw InvalidValueException("Condicao", getStringRes(R.string.nenhuma_condicao))
        } else if (companies.isNullOrEmpty()) {
            throw InvalidValueException("Empresa", getStringRes(R.string.nenhuma_empresa))
        }

        if (sale.num_codigo_online.isNullOrEmpty())
            sale.dat_emissao = Date()
        sale.fky_empresa = companies[companyPosition]
        sale.fky_condicao_pagamento = conditions[conditionPosition]

        buildItemsList(sale)

        this.sale.value?.copy(sale)
    }


    private fun buildItemsList(sale: Sale) {
        val itemsBeforeClear: MutableList<SaleItem> = mutableListOf()
        itemsBeforeClear.addAll(items.sortedWith(compareBy(SaleItem::cod_sequencia)))

        items.clear()
        products.keys.forEach { product ->
            products[product]?.let { detail ->
                val item = SaleItem()
                item.num_codigo_online = sale.num_codigo_online
                item.cod_sequencia = getSequenceCode(itemsBeforeClear, product)
                item.fky_produto = product
                item.fky_empresa = product.fky_empresa!!
                item.fky_unidade_medida = product.fky_unidade_medida!!
                item.fky_cor = detail.colors[0].cod_cor
                item.dsc_observacao = product.dsc_observacao
                item.dbl_quantidade = detail.quantity.toDouble()
                item.dbl_preco_unit = when (sale.fky_condicao_pagamento.flg_a_vista) {
                    EYesNo.S -> product.dbl_venda_vista!!
                    EYesNo.N -> product.dbl_venda_prazo!!
                }
                item.dbl_total_item = item.dbl_preco_unit * item.dbl_quantidade
                val currentItem = itemsBeforeClear.find {
                    it.fky_produto.cod_produto == product.cod_produto &&
                            it.num_codigo_online == sale.num_codigo_online
                }
                currentItem?.let {
                    item.version = it.version
                }
                items.add(item)
            }
        }

        initRemovedItemsList(itemsBeforeClear)
    }

    fun save(firebaseToken: String) {
        if (sale.value?.num_codigo_online.isNullOrEmpty())
             insertResponse = saleRepository.insert(getJsonRequest("venda", sale.value!!, firebaseToken))
        else updateResponse = saleRepository.update(getJsonRequest("venda", sale.value!!, firebaseToken))
    }

    fun insertItems() {
        items.forEach { it.num_codigo_online = sale.value?.num_codigo_online!! }
        itemInsertResponse = saleItemRepository.insert(items)
    }

    fun updateItems() {
        itemUpdateResponse = saleItemRepository.update(items)
    }

    fun deleteItems() {
        itemDeleteResponse = saleItemRepository.delete(removedItems)
    }

    fun findItemBy(sale: Sale) {
        items.clear()
        itemResponse = saleItemRepository.findAll(sale.num_codigo_online)
    }

    fun addSelectedProducts(productsSelected: List<Product>?) {
        productsSelected?.let { selected ->
            val productsFiltered = selected.filter {
                products.keys.find { p -> p.cod_produto == it.cod_produto } == null
            }
            this.productsSelected.clear()
            this.productsSelected.addAll(productsFiltered)
        }
    }

    private fun initRemovedItemsList(itemsBeforeClear: MutableList<SaleItem>) {
        val removedProducts = this.removedProducts.keys.filter { product ->
            items.find { it.fky_produto.cod_produto == product.cod_produto } == null
        }
        val removedProductIds = removedProducts.map { it.cod_produto }

        removedItems.clear()
        removedItems.addAll(itemsBeforeClear.filter {
            val removedProductId = removedProductIds.find { id -> id == it.fky_produto.cod_produto }
            removedProductId != null
        } as MutableList<SaleItem>)
    }

    private fun getSequenceCode(itemsBeforeClear: MutableList<SaleItem>, product: Product): Int {
        val item: SaleItem? = itemsBeforeClear.find { it.fky_produto.cod_produto == product.cod_produto }
        if (item != null)
            return item.cod_sequencia
        return items.size + 1
    }

    fun initProductColors(i: Int = 0, done: (selected: MutableList<Product>, colors: MutableList<ProductColor>) -> Unit) {
        if (i == productsSelected.size) {
            done(productsSelected, productsColors)
            return
        }

        var count = i
        val product = productsSelected[count]
        productColorRepository.findBy(product.cod_produto!!, { productColors ->
            productColors?.let {
                it.forEach { color ->
                    val productColor = productsColors.find { productColor ->
                        productColor.cod_produto.cod_produto == color.cod_produto.cod_produto &&
                                productColor.cod_cor.cod_cor == color.cod_cor.cod_cor
                    }
                    if (productColor == null) {
                        productsColors.add(color)
                    } else {
                        val index = productsColors.indexOf(color)
                        if (index >= 0) {
                            productsColors.add(index, color)
                        } else {
                            productsColors.clear()
                            productsColors.add(color)
                        }
                    }
                }
                count += 1
            }
        }, {
            count = productsSelected.size
        }, {
            initProductColors(count, done)
        })
    }

    fun getTotalItems(conditionPosition: Int): Double {
        var totalItems = 0.0
        products.keys.forEach { product ->
            products[product]?.let {
                val quantity = it.quantity
                val paymentCondition = conditions[conditionPosition]

                product.dbl_venda_vista?.let { venda_vista ->
                    product.dbl_venda_prazo?.let { venda_prazo ->
                        when {
                            paymentCondition.cod_condicao_pagamento == 0 -> totalItems += (venda_vista * quantity)
                            paymentCondition.flg_a_vista == EYesNo.S -> totalItems += (venda_vista * quantity)
                            paymentCondition.flg_a_vista == EYesNo.N -> totalItems += (venda_prazo * quantity)
                        }
                    }
                }
            }
        }

        return totalItems
    }

    fun getTotalQuantity(): Int {
        var quantity = 0
        if (products.values.isNotEmpty())
            products.values.forEach { quantity += it.quantity }
        return quantity
    }

    fun convertItemsToProductDetail() {
        products.clear()
        items.forEach {
            val productColor = ProductColor(it.fky_produto, it.fky_cor)
            products[it.fky_produto] = ProductDetail(it.dbl_quantidade.toInt(), mutableListOf(productColor))
        }
    }
}