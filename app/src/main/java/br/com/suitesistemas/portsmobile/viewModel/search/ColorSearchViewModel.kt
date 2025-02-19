package br.com.suitesistemas.portsmobile.viewModel.search

import br.com.suitesistemas.portsmobile.model.entity.Color
import br.com.suitesistemas.portsmobile.service.color.ColorRepository

class ColorSearchViewModel : SelectSearchViewModel<Color>() {

    override fun initRepository(company: String) {
        companyName = company
        repository = ColorRepository(company)
    }

    override fun sortingList(list: MutableList<Color>) = list.sortedWith(compareBy(Color::dsc_cor))

}