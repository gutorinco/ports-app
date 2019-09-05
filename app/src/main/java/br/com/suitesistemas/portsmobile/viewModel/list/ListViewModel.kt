package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation

abstract class ListViewModel<T> : ViewModel() {

    var response = MutableLiveData<ApiResponse<MutableList<T>?>>()
    var rollbackResponse = MutableLiveData<ApiResponse<T?>>()
    var refreshResponse = MutableLiveData<ApiResponse<MutableList<T>?>>()
    protected var companyName: String = ""
    protected var removedPosition: Int = 0
    protected var removedObject: T? = null
    protected val list: MutableList<T> = mutableListOf()

    fun getBy(position: Int) = list[position]
    fun listIsEmpty() = list.isNullOrEmpty()

    fun addAll(list: List<T>) {
        this.list.addAll(list)
    }

    fun add(obj: T, operation: EHttpOperation = EHttpOperation.POST) {
        if (operation == EHttpOperation.ROLLBACK)
            list.add(removedPosition, obj)
        else list.add(obj)
        response.value = ApiResponse(getSortingList(), operation)
    }

    abstract fun sortingList(): List<T>
    fun getSortingList(): MutableList<T> {
        val sortedList = sortingList()
        if (sortedList.isNullOrEmpty())
            return mutableListOf()

        val list: MutableList<T> = mutableListOf()
        list.addAll(sortedList)

        this.list.clear()
        this.list.addAll(list)

        return list
    }

    protected fun getJsonRequest(objName: String): MutableList<HashMap<String, Any?>> {
        val map = HashMap<String, Any?>()
        map[objName] = removedObject
        map["token"] = null

        val jsonRequest: MutableList<HashMap<String, Any?>> = mutableListOf()
        jsonRequest.add(map)

        return jsonRequest
    }

}