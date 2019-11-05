package br.com.suitesistemas.portsmobile.viewModel.search

import br.com.suitesistemas.portsmobile.model.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.service.financial_release.FinancialReleaseRepository

class FinancialReleaseSearchViewModel : SearchViewModel<FinancialRelease>() {

    override fun initRepository(company: String) {
        companyName = company
        repository = FinancialReleaseRepository(company)
    }

    override fun sortingList(list: MutableList<FinancialRelease>) = list.sortedWith(compareBy(FinancialRelease::dat_emissao)).asReversed()

}