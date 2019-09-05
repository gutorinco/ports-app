package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.suitesistemas.portsmobile.model.ApiResponse

abstract class SearchViewModel<T> : ViewModel() {

    @Bindable val searching = MutableLiveData<Boolean>()
    @Bindable val wasSearched = MutableLiveData<Boolean>()
    @Bindable val listIsEmpty = MutableLiveData<Boolean>()
    var response = MutableLiveData<ApiResponse<MutableList<T>?>>()
    protected var removedPosition: Int = 0
    protected var removedObject: T? = null
    protected lateinit var companyName: String

    init {
        searching.value = false
        wasSearched.value = false
        listIsEmpty.value = false
    }

    abstract fun addAll(list: MutableList<T>)
    abstract fun search(search: String)
    abstract fun getList(): MutableList<T>

    protected fun getListCopy(originalList: MutableList<T>): MutableList<T> {
        val list: MutableList<T> = mutableListOf()
        list.addAll(originalList)
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