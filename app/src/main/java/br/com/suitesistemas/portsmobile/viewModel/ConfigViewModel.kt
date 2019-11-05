package br.com.suitesistemas.portsmobile.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Configuration
import br.com.suitesistemas.portsmobile.service.configuration.ConfigurationRepository

class ConfigViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var configRepository: ConfigurationRepository
    var response = MutableLiveData<ApiResponse<MutableList<Configuration>?>>()
    val config = Configuration()

    fun initRepositories(companyName: String) {
        configRepository = ConfigurationRepository(companyName)
    }

    fun fetchConfigurations(){
        response = configRepository.findAll()
    }

}