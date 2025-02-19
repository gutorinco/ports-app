package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.VersionResponse
import br.com.suitesistemas.portsmobile.model.entity.Company
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository

abstract class FormViewModel<T>(application: Application) : AndroidViewModel(application) {

    var updateResponse = MutableLiveData<ApiResponse<VersionResponse?>>()
    var insertResponse = MutableLiveData<ApiResponse<T?>>()
    var companiesResponse = MutableLiveData<ApiResponse<MutableList<Company>?>>()
    val companies: MutableList<Company> = mutableListOf()
    protected lateinit var companyRepository: CompanyRepository

    protected fun <K> getApiResponseFromExistList(list: MutableList<K>): MutableLiveData<ApiResponse<MutableList<K>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<K>?>>()
        apiResponse.value = ApiResponse(list, EHttpOperation.GET)
        return apiResponse
    }

    fun fetchAllCompanies() {
        companiesResponse = when (companies.isNullOrEmpty()) {
            true -> companyRepository.findAll()
            false -> getApiResponseFromExistList(companies)
        }
    }

    fun addAllCompanies(companies: MutableList<Company>) {
        this.companies.addAll(companies)
    }

    fun getCompaniesNames() = companies.map { company -> company.dsc_empresa }

    open fun getCompanyIndex(): Int = 0

    protected fun getJsonRequest(objName: String, obj: T, firebaseToken: String): MutableList<HashMap<String, Any?>> {
        val jsonRequest: MutableList<HashMap<String, Any?>> = mutableListOf()

        val objMap = HashMap<String, Any?>()
        objMap[objName] = obj

        jsonRequest.add(objMap)

        val tokenMap = HashMap<String, Any?>()
        tokenMap["token"] = firebaseToken

        jsonRequest.add(tokenMap)

        return jsonRequest
    }

    protected fun getStringRes(stringResource: Int, param: String? = null): String {
        if (param == null)
            return getApplication<Application>().resources.getString(stringResource)
        return getApplication<Application>().resources.getString(stringResource, param)
    }

}