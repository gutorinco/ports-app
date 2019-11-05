package br.com.suitesistemas.portsmobile.viewModel.list

import br.com.suitesistemas.portsmobile.model.entity.Color
import br.com.suitesistemas.portsmobile.service.color.ColorRepository
import br.com.suitesistemas.portsmobile.viewModel.completeList.ListViewModel

class ColorViewModel : ListViewModel<Color, ColorRepository>("cor") {

    var color: Color? = Color()

    override fun initRepositories(companyName: String) {
        this.companyName = companyName
        repository = ColorRepository(companyName)
    }

    override fun sortingList(list: List<Color>) = list.sortedWith(compareBy(Color::dsc_cor))

}