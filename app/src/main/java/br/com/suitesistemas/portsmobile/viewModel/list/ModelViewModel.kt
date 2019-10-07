package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.entity.Model
import br.com.suitesistemas.portsmobile.entity.ModelCombination
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.service.model.ModelRepository
import br.com.suitesistemas.portsmobile.service.model_combination.ModelCombinationRepository

class ModelViewModel : ListViewModel<Model>() {

    private lateinit var repository: ModelRepository
    private lateinit var modelCombinationRepository: ModelCombinationRepository
    var modelCombinationResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    var modelCombinationRollbackResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    val removedModelCombinations: MutableList<ModelCombination> = mutableListOf()

    fun fetchAllModels(companyName: String) {
        this.companyName = companyName
        repository = ModelRepository(companyName)
        modelCombinationRepository = ModelCombinationRepository(companyName)
        response = repository.findAll()
    }

    fun refresh() {
        list.clear()
        refreshResponse = repository.findAll()
    }

    fun updateList(modelResponse: Model) {
        for (model in list)
            if (model.num_codigo_online == modelResponse.num_codigo_online)
                model.copy(modelResponse)
        response.value = ApiResponse(getSortingList(), EHttpOperation.PUT)
    }

    fun addRemovedModelCombinations(colors: MutableList<ModelCombination>) {
        removedModelCombinations.clear()
        removedModelCombinations.addAll(colors)
    }

    fun fetchAllCombinationsBy(position: Int) {
        val model = getBy(position)
        modelCombinationResponse = modelCombinationRepository.findBy(model.num_codigo_online)
    }

    fun modelCombinationsDeleteRollback() {
        removedModelCombinations.forEach { it.cod_modelo = removedObject!! }
        modelCombinationRollbackResponse = modelCombinationRepository.insert(removedModelCombinations)
    }

    fun delete(position: Int, firebaseToken: String) {
        val model = getBy(position)
        repository.delete(model.num_codigo_online, firebaseToken, {
            removedObject = model
            removedPosition = position
            list.removeAt(position)
            response.value = ApiResponse(getSortingList(), EHttpOperation.DELETE)
        }, {
            response.value = ApiResponse(it!!, EHttpOperation.DELETE)
        })
    }

    fun deleteRollback() {
        rollbackResponse = repository.insert(getJsonRequest("modelo"))
    }

    override fun sortingList() = list.sortedWith(compareBy(Model::dsc_modelo))

}