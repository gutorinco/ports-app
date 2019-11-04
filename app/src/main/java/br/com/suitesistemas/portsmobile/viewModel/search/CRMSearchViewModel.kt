package br.com.suitesistemas.portsmobile.viewModel.search

import br.com.suitesistemas.portsmobile.entity.CRM
import br.com.suitesistemas.portsmobile.service.crm.CRMRepository

class CRMSearchViewModel : SelectSearchViewModel<CRM>() {

    override fun initRepository(company: String) {
        companyName = company
        repository = CRMRepository(company)
    }

    override fun sortingList(list: MutableList<CRM>) = list.sortedWith(compareBy(CRM::dat_cadastro)).asReversed()

}