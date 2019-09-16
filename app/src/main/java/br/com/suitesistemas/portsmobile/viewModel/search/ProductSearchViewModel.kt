package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.service.product.ProductRepository

class ProductSearchViewModel : SearchViewModel<Product>() {

    @Bindable
    val selected = MutableLiveData<Boolean>()
    private val products: MutableList<Product> = mutableListOf()
    private val productsSelected: MutableList<Product> = mutableListOf()
    private lateinit var selectedResponse: (Boolean) -> Unit
    private lateinit var repository: ProductRepository

    init {
        selected.value = false
    }

    fun onSelected(selected: (Boolean) -> Unit) {
        selectedResponse = selected
    }

    fun initRepository(companyName: String) {
        this.companyName = companyName
        repository = ProductRepository(companyName)
    }

    override fun addAll(list: MutableList<Product>) {
        products.addAll(list)
        listIsEmpty.value = products.isNullOrEmpty()
    }

    override fun search(search: String) {
        products.clear()
        searching.value = true
        wasSearched.value = true
        response = repository.search(search)
    }

    override fun getList(): MutableList<Product> = getListCopy(products)

    fun getSelectedList(): MutableList<Product> = getListCopy(productsSelected)

    fun onChecked(checked: Boolean, position: Int) {
        val product = products[position]
        when (checked) {
            true -> productsSelected.add(product)
            false -> productsSelected.remove(product)
        }
        selected.value = productsSelected.isNotEmpty()
        selectedResponse.invoke(productsSelected.isNotEmpty())
    }

}