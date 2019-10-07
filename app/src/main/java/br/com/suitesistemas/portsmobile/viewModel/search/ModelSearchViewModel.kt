package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Model
import br.com.suitesistemas.portsmobile.entity.ModelCombination
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.service.model.ModelRepository
import br.com.suitesistemas.portsmobile.service.model_combination.ModelCombinationRepository

class ModelSearchViewModel : SelectSearchViewModel<Model>() {

    var modelCombinationResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    var modelCombinationRollbackResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    val removedModelCombinations: MutableList<ModelCombination> = mutableListOf()
    private lateinit var modelCombinationRepository: ModelCombinationRepository

    override fun initRepository(company: String) {
        companyName = company
        repository = ModelRepository(company)
        modelCombinationRepository = ModelCombinationRepository(company)
    }

    fun fetchAllCombinationsBy(position: Int) {
        val model = getBy(position)
        deletedOnSearch = true
        modelCombinationResponse = modelCombinationRepository.findBy(model.num_codigo_online)
    }

    fun addRemovedModelCombinations(combinations: MutableList<ModelCombination>) {
        removedModelCombinations.clear()
        removedModelCombinations.addAll(combinations)
    }

    fun modelCombinationsDeleteRollback() {
        removedModelCombinations.forEach { it.cod_modelo = removedObject!! }
        modelCombinationRollbackResponse = modelCombinationRepository.insert(removedModelCombinations)
    }

}