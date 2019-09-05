package br.com.suitesistemas.portsmobile.viewModel.search

import br.com.suitesistemas.portsmobile.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.service.financial_release.FinancialReleaseRepository

class FinancialReleaseSearchViewModel : SearchViewModel<FinancialRelease>() {

    private val releases: MutableList<FinancialRelease> = mutableListOf()
    private lateinit var repository: FinancialReleaseRepository

    fun initRepository(companyName: String) {
        this.companyName = companyName
        repository = FinancialReleaseRepository(companyName)
    }

    override fun search(search: String) {
        wasSearched.value = true
        releases.clear()
        searching.value = true
        response = repository.search(search)
    }

    override fun addAll(list: MutableList<FinancialRelease>) {
        releases.addAll(list)
        listIsEmpty.value = releases.isNullOrEmpty()
    }

    override fun getList(): MutableList<FinancialRelease> = getListCopy(releases)

}