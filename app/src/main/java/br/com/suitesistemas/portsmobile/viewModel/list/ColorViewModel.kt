package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.color.ColorRepository

class ColorViewModel : ListViewModel<Color>() {

    private lateinit var repository: ColorRepository
    var updateResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
    var insertResponse = MutableLiveData<ApiResponse<Color?>>()
    var color: Color? = Color()

    fun fetchAllColors(companyName: String) {
        this.companyName = companyName
        repository = ColorRepository(companyName)
        response = repository.findAll()
    }

    fun refresh() {
        list.clear()
        refreshResponse = repository.findAll()
    }

    fun updateList(colorResponse: Color) {
        for (color in list)
            if (color.num_codigo_online == colorResponse.num_codigo_online)
                color.copy(colorResponse)
        response.value = ApiResponse(getSortingList(), EHttpOperation.PUT)
    }

    fun delete(position: Int, firebaseToken: String) {
        val color = getBy(position)
        repository.delete(color.num_codigo_online, firebaseToken, {
            removedObject = color
            removedPosition = position
            list.removeAt(position)
            response.value = ApiResponse(getSortingList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })
    }

    fun deleteRollback() {
        rollbackResponse = repository.insert(getJsonRequest("cor"))
    }

    fun save(color: Color, firebaseToken: String) {
        if (color.num_codigo_online.isEmpty())
             insert(color, firebaseToken)
        else update(color, firebaseToken)
    }

    private fun insert(color: Color, firebaseToken: String) {
        insertResponse = repository.insert(getJsonRequest("cor", color, firebaseToken))
    }

    private fun update(color: Color, firebaseToken: String) {
        updateResponse = repository.update(getJsonRequest("cor", color, firebaseToken))
    }

    override fun sortingList() = list.sortedWith(compareBy(Color::dsc_cor))

}