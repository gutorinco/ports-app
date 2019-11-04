package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.ListService

abstract class SearchViewModel<T> : ViewModel() {

    @Bindable val searching = MutableLiveData<Boolean>()
    @Bindable val wasSearched = MutableLiveData<Boolean>()
    @Bindable val listIsEmpty = MutableLiveData<Boolean>()
    protected var removedPosition: Int = 0
    protected lateinit var repository: ListService<T>
    protected lateinit var companyName: String
    var removedObject: T? = null
    var deletedOnSearch: Boolean = false
    var response = MutableLiveData<ApiResponse<MutableList<T>?>>()
    var rollbackResponse = MutableLiveData<ApiResponse<T?>>()
    val completeList: MutableList<T> = mutableListOf()

    init {
        searching.value = false
        wasSearched.value = false
        listIsEmpty.value = false
    }

    abstract fun initRepository(company: String)

    fun add(product: T, operation: EHttpOperation = EHttpOperation.POST) {
        with (completeList) {
            if (operation == EHttpOperation.ROLLBACK)
                 add(removedPosition, product)
            else add(product)

            val sortedList = sortingList(this)
            clear()
            addAll(sortedList)
            response.value = ApiResponse(this, operation)
        }
    }

    fun addAll(list: MutableList<T>) {
        val sortedList = sortingList(list)
        completeList.addAll(sortedList)
        listIsEmpty.value = completeList.isNullOrEmpty()
    }

    open fun search(search: String) {
        completeList.clear()
        searching.value = true
        wasSearched.value = true
        response = repository.search(search)
    }

    fun getList() = getListCopy(completeList)

    fun getBy(position: Int) = completeList[position]

    fun delete(id: String, position: Int, firebaseToken: String) {
        deletedOnSearch = true
        val obj = completeList[position]
        repository.delete(id, firebaseToken, {
            removedObject = obj
            removedPosition = position
            completeList.removeAt(position)
            response.value = ApiResponse(completeList, EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })
    }

    fun deleteRollback(jsonObjName: String) {
        rollbackResponse = repository.insert(getJsonRequest(jsonObjName))
    }

    protected fun getListCopy(originalList: MutableList<T>): MutableList<T> {
        val list: MutableList<T> = mutableListOf()
        list.addAll(originalList)
        return list
    }

    private fun getJsonRequest(objName: String): MutableList<HashMap<String, Any?>> {
        val map = HashMap<String, Any?>()
        map[objName] = removedObject
        map["token"] = null

        val jsonRequest: MutableList<HashMap<String, Any?>> = mutableListOf()
        jsonRequest.add(map)

        return jsonRequest
    }

    abstract fun sortingList(list: MutableList<T>): List<T>

}