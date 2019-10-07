package br.com.suitesistemas.portsmobile.viewModel.search

import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.service.color.ColorRepository

class ColorSearchViewModel : SelectSearchViewModel<Color>() {

    override fun initRepository(company: String) {
        companyName = company
        repository = ColorRepository(company)
    }

}