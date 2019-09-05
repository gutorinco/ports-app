package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.customer.CustomerRepository

class PeopleSearchViewModel : SearchViewModel<Customer>() {

    var rollbackResponse = MutableLiveData<ApiResponse<Customer?>>()
    private val peoples: MutableList<Customer> = mutableListOf()
    private lateinit var type: String
    private lateinit var repository: CustomerRepository

    fun initRepository(companyName: String, type: String) {
        this.companyName = companyName
        this.type = type
        repository = CustomerRepository(companyName)
    }

    override fun addAll(list: MutableList<Customer>) {
        peoples.addAll(list)
        listIsEmpty.value = peoples.isNullOrEmpty()
    }

    fun add(people: Customer, operation: EHttpOperation = EHttpOperation.POST) {
        if (operation == EHttpOperation.ROLLBACK)
             peoples.add(removedPosition, people)
        else peoples.add(people)
        response.value = ApiResponse(getList(), operation)
    }

    override fun search(search: String) {
        peoples.clear()
        searching.value = true
        wasSearched.value = true
        response = repository.search(search, type)
    }

    override fun getList(): MutableList<Customer> = getListCopy(peoples)

    fun getCustomerBy(position: Int) = peoples[position]

    fun delete(position: Int) {
        val people = peoples[position]
        repository.delete(people.num_codigo_online, {
            removedObject = people
            removedPosition = position
            peoples.removeAt(position)
            response.value = ApiResponse(getList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })
    }

    fun deleteRollback() {
        rollbackResponse = repository.insert(getJsonRequest("pessoa"))
    }

}