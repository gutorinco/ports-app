package br.com.suitesistemas.portsmobile.viewModel.list

import br.com.suitesistemas.portsmobile.model.entity.Customer
import br.com.suitesistemas.portsmobile.service.customer.CustomerRepository
import br.com.suitesistemas.portsmobile.viewModel.completeList.ListViewModel

class CustomerViewModel : ListViewModel<Customer, CustomerRepository>("pessoa") {

    override fun initRepositories(company: String) {
        companyName = company
        repository = CustomerRepository(company)
    }

    override fun sortingList(list: List<Customer>) = list.sortedWith(compareBy(Customer::dsc_nome_pessoa))

}