package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.color.ColorRepository

class ColorSearchViewModel : SearchViewModel<Color>() {

    var rollbackResponse = MutableLiveData<ApiResponse<Color?>>()
    private val colors: MutableList<Color> = mutableListOf()
    private lateinit var repository: ColorRepository

    fun initRepository(companyName: String) {
        this.companyName = companyName
        repository = ColorRepository(companyName)
    }

    override fun addAll(list: MutableList<Color>) {
        colors.addAll(list)
        listIsEmpty.value = colors.isNullOrEmpty()
    }

    fun add(color: Color, operation: EHttpOperation = EHttpOperation.POST) {
        if (operation == EHttpOperation.ROLLBACK)
            colors.add(removedPosition, color)
        else colors.add(color)
        response.value = ApiResponse(getList(), operation)
    }

    override fun search(search: String) {
        colors.clear()
        searching.value = true
        wasSearched.value = true
        response = repository.search(search)
    }

    override fun getList(): MutableList<Color> = getListCopy(colors)

    fun getColorBy(position: Int) = colors[position]

    fun delete(position: Int, firebaseToken: String) {
        val color = colors[position]
        repository.delete(color.num_codigo_online, firebaseToken, {
            removedObject = color
            removedPosition = position
            colors.removeAt(position)
            response.value = ApiResponse(getList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })
    }

    fun deleteRollback() {
        rollbackResponse = repository.insert(getJsonRequest("cor"))
    }

}