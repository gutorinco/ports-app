package br.com.suitesistemas.portsmobile.viewModel.list

import br.com.suitesistemas.portsmobile.model.entity.CRM
import br.com.suitesistemas.portsmobile.service.crm.CRMRepository
import br.com.suitesistemas.portsmobile.viewModel.completeList.ListViewModel

class CRMViewModel : ListViewModel<CRM, CRMRepository>("crm") {

    var crm: CRM? = CRM()

    override fun initRepositories(companyName: String) {
        this.companyName = companyName
        repository = CRMRepository(companyName)
    }

    override fun sortingList(list: List<CRM>) = list.sortedWith(compareBy(CRM::dat_cadastro)).asReversed()

}