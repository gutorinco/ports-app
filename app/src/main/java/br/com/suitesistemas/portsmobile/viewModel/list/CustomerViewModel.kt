package br.com.suitesistemas.portsmobile.viewModel.list

import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.customer.CustomerRepository

class CustomerViewModel : ListViewModel<Customer>() {

    private lateinit var repository: CustomerRepository

    fun fetchAllCustomers(companyName: String) {
        this.companyName = companyName
        repository = CustomerRepository(companyName)
        response = repository.findAll()
    }

    fun refresh() {
        list.clear()
        refreshResponse = repository.findAll()
    }

    fun updateList(customerResponse: Customer) {
        for (customer in list)
            if (customer.num_codigo_online == customerResponse.num_codigo_online)
                customer.copy(customerResponse)
        response.value = ApiResponse(getSortingList(), EHttpOperation.PUT)
    }

    fun delete(position: Int) {
        val customer = getBy(position)
        repository.delete(customer.num_codigo_online, {
            removedObject = customer
            removedPosition = position
            list.removeAt(position)
            response.value = ApiResponse(getSortingList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })
    }

    fun deleteRollback() {
        rollbackResponse = repository.insert(getJsonRequest("pessoa"))
    }

    override fun sortingList() = list.sortedWith(compareBy(Customer::dsc_nome_pessoa))

}