package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Company
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation

abstract class FormViewModel<T>(application: Application) : AndroidViewModel(application) {

    var updateResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
    var insertResponse = MutableLiveData<ApiResponse<T?>>()
    var companiesResponse = MutableLiveData<ApiResponse<MutableList<Company>?>>()
    protected val companies: MutableList<Company> = mutableListOf()

    protected fun <K> getApiResponseFromExistList(list: MutableList<K>): MutableLiveData<ApiResponse<MutableList<K>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<K>?>>()
        apiResponse.value = ApiResponse(list, EHttpOperation.GET)
        return apiResponse
    }

    fun addAllCompanies(companies: MutableList<Company>) {
        this.companies.addAll(companies)
    }

    protected fun getJsonRequest(objName: String, obj: T): MutableList<HashMap<String, Any?>> {
        val map = HashMap<String, Any?>()
        map[objName] = obj
        map["token"] = null

        val jsonRequest: MutableList<HashMap<String, Any?>> = mutableListOf()
        jsonRequest.add(map)

        return jsonRequest
    }

    protected fun getStringRes(stringResource: Int, param: String? = null): String {
        if (param == null)
            return getApplication<Application>().resources.getString(stringResource)
        return getApplication<Application>().resources.getString(stringResource, param)
    }

}