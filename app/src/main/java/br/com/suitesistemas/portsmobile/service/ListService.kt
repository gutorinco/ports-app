package br.com.suitesistemas.portsmobile.service

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse

interface ListService<T> {
    fun findAll(): MutableLiveData<ApiResponse<MutableList<T>?>>
    fun search(search: String): MutableLiveData<ApiResponse<MutableList<T>?>>
    fun update(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<VersionResponse?>>
    fun insert(json: MutableList<HashMap<String, Any?>>): MutableLiveData<ApiResponse<T?>>
    fun delete(id: String, firebaseToken: String, success: () -> Unit, failure: (messageError: String?) -> Unit)
}