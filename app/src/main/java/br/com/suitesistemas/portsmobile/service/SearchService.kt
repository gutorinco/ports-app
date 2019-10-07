package br.com.suitesistemas.portsmobile.service

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.model.ApiResponse

interface SearchService<T> {
    fun delete(id: String, firebaseToken: String, success: () -> Unit, failure: (messageError: String?) -> Unit)
    fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<T?>>
    fun search(search: String): MutableLiveData<ApiResponse<MutableList<T>?>>
}