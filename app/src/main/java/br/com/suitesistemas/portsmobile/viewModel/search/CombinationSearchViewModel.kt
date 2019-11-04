package br.com.suitesistemas.portsmobile.viewModel.search

import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.service.combination.CombinationRepository

class CombinationSearchViewModel : SelectSearchViewModel<Combination>() {

    override fun initRepository(company: String) {
        companyName = company
        repository = CombinationRepository(company)
    }

    override fun sortingList(list: MutableList<Combination>) = list.sortedWith(compareBy(Combination::dsc_combinacao))

}