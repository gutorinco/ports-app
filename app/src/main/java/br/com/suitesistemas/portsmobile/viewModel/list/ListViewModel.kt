package br.com.suitesistemas.portsmobile.viewModel.completeList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.suitesistemas.portsmobile.entity.ChangeableModel
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.ListService

abstract class ListViewModel<T : ChangeableModel<T>, K : ListService<T>>(private val jsonName: String) : ViewModel() {

    protected lateinit var repository: K
    var updateResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
    var insertResponse = MutableLiveData<ApiResponse<T?>>()
    var response = MutableLiveData<ApiResponse<MutableList<T>?>>()
    var rollbackResponse = MutableLiveData<ApiResponse<T?>>()
    var refreshResponse = MutableLiveData<ApiResponse<MutableList<T>?>>()
    var removedObject: T? = null
    protected var companyName: String = ""
    protected var removedPosition: Int = 0
    protected val completeList: MutableList<T> = mutableListOf()

    abstract fun initRepositories(company: String)

    fun fetchAll() {
        response = repository.findAll()
    }

    fun getBy(position: Int) = completeList[position]

    fun getList(): MutableList<T> {
        val completeList: MutableList<T> = mutableListOf()
        completeList.addAll(this.completeList)
        return completeList
    }

    fun listIsEmpty() = completeList.isNullOrEmpty()

    fun addAll(completeList: List<T>) {
        val sortedList = sortingList(completeList)
        this.completeList.addAll(sortedList)
    }

    fun add(obj: T, operation: EHttpOperation = EHttpOperation.POST) {
        if (operation == EHttpOperation.ROLLBACK)
             completeList.add(removedPosition, obj)
        else completeList.add(obj)

        sortListAndSendResponse(operation)
    }

    fun updateList(objResponse: T) {
        for (obj in completeList)
            if (obj == objResponse)
                obj.copy(objResponse)
        sortListAndSendResponse(EHttpOperation.PUT)
    }

    private fun sortListAndSendResponse(operation: EHttpOperation) {
        with (completeList) {
            val sortedList = sortingList(this)
            clear()
            addAll(sortedList)
            response.value = ApiResponse(this, operation)
        }
    }

    fun refresh() {
        completeList.clear()
        refreshResponse = repository.findAll()
    }

    fun delete(position: Int, firebaseToken: String) {
        val obj = getBy(position)
        repository.delete(obj.getId(), firebaseToken, {
            removedObject = obj
            removedPosition = position
            completeList.removeAt(position)
            response.value = ApiResponse(completeList, EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })
    }

    fun deleteRollback() {
        rollbackResponse = repository.insert(getJsonRequest(jsonName))
    }

    fun insert(obj: T, firebaseToken: String) {
        insertResponse = repository.insert(getJsonRequest(jsonName, obj, firebaseToken))
    }

    fun update(obj: T, firebaseToken: String) {
        updateResponse = repository.update(getJsonRequest(jsonName, obj, firebaseToken))
    }

    abstract fun sortingList(list: List<T>): List<T>

    private fun getJsonRequest(objName: String, obj: T? = removedObject, token: String? = null): MutableList<HashMap<String, Any?>> {
        val map = HashMap<String, Any?>()
        map[objName] = obj
        map["token"] = token

        val jsonRequest: MutableList<HashMap<String, Any?>> = mutableListOf()
        jsonRequest.add(map)

        return jsonRequest
    }

}