package br.com.suitesistemas.portsmobile.viewModel.list

import br.com.suitesistemas.portsmobile.model.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.service.financial_release.FinancialReleaseRepository
import br.com.suitesistemas.portsmobile.viewModel.completeList.ListViewModel

class FinancialReleaseViewModel : ListViewModel<FinancialRelease, FinancialReleaseRepository>("lancamento") {

    override fun initRepositories(company: String) {
        companyName = company
        repository = FinancialReleaseRepository(company)
    }

    override fun sortingList(list: List<FinancialRelease>) = list.sortedWith(compareBy(FinancialRelease::dat_emissao)).asReversed()

}