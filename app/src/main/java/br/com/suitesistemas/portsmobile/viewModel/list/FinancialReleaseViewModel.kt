package br.com.suitesistemas.portsmobile.viewModel.list

import br.com.suitesistemas.portsmobile.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.service.financial_release.FinancialReleaseRepository

class FinancialReleaseViewModel : ListViewModel<FinancialRelease>() {

    private lateinit var repository: FinancialReleaseRepository

    fun fetchAllFinancialReleases(companyName: String) {
        this.companyName = companyName
        repository = FinancialReleaseRepository(companyName)
        response = repository.findAll()
    }

    fun refresh() {
        list.clear()
        refreshResponse = repository.findAll()
    }

    override fun sortingList() = list.sortedWith(compareBy(FinancialRelease::dat_emissao)).asReversed()

}