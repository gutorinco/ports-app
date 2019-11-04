package br.com.suitesistemas.portsmobile.viewModel.search

import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.service.customer.CustomerRepository

class PeopleSearchViewModel : SearchViewModel<Customer>() {

    private lateinit var type: String

    override fun initRepository(company: String) {
        TODO("Not implemented!")
    }

    fun initRepository(companyName: String, type: String) {
        this.companyName = companyName
        this.type = type
        repository = CustomerRepository(companyName)
    }

    override fun search(search: String) {
        completeList.clear()
        searching.value = true
        wasSearched.value = true
        response = (repository as CustomerRepository).search(search, type)
    }

    override fun sortingList(list: MutableList<Customer>) = list.sortedWith(compareBy(Customer::dsc_nome_pessoa))

}