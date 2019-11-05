package br.com.suitesistemas.portsmobile.viewModel.list

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Model
import br.com.suitesistemas.portsmobile.model.entity.ModelCombination
import br.com.suitesistemas.portsmobile.model.entity.ModelGridCombination
import br.com.suitesistemas.portsmobile.service.model.ModelRepository
import br.com.suitesistemas.portsmobile.service.model_combination.ModelCombinationRepository
import br.com.suitesistemas.portsmobile.service.model_grid_combination.ModelGridCombinationRepository
import br.com.suitesistemas.portsmobile.viewModel.completeList.ListViewModel

class ModelViewModel : ListViewModel<Model, ModelRepository>("modelo") {

    var modelCombinationResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    var modelCombinationRollbackResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    var modelGridResponse = MutableLiveData<ApiResponse<MutableList<ModelGridCombination>?>>()
    var modelGridRollbackResponse = MutableLiveData<ApiResponse<MutableList<ModelGridCombination>?>>()
    val removedModelCombinations: MutableList<ModelCombination> = mutableListOf()
    val removedModelGrids: MutableList<ModelGridCombination> = mutableListOf()
    private lateinit var modelCombinationRepository: ModelCombinationRepository
    private lateinit var modelGridRepository: ModelGridCombinationRepository

    override fun initRepositories(company: String) {
        companyName = company
        repository = ModelRepository(company)
        modelGridRepository = ModelGridCombinationRepository(company)
        modelCombinationRepository = ModelCombinationRepository(company)
    }

    fun addRemovedModelCombinations(colors: MutableList<ModelCombination>) {
        removedModelCombinations.clear()
        removedModelCombinations.addAll(colors)
    }

    fun addRemovedModelGridCombinations(colors: MutableList<ModelGridCombination>) {
        removedModelGrids.clear()
        removedModelGrids.addAll(colors)
    }

    fun fetchAllCombinationsBy(position: Int) {
        val model = getBy(position)
        modelCombinationResponse = modelCombinationRepository.findBy(model.num_codigo_online)
    }

    fun fetchAllGridCombinationsBy(position: Int) {
        val model = getBy(position)
        modelGridResponse = modelGridRepository.findBy(model.num_codigo_online)
    }

    fun modelCombinationsDeleteRollback() {
        removedModelCombinations.forEach { it.cod_modelo = removedObject!! }
        modelCombinationRollbackResponse = modelCombinationRepository.insert(removedModelCombinations)
    }

    fun modelGridCombinationsDeleteRollback() {
        removedModelGrids.forEach { it.cod_modelo = removedObject!! }
        modelGridRollbackResponse = modelGridRepository.insert(removedModelGrids)
    }

    override fun sortingList(list: List<Model>) = list.sortedWith(compareBy(Model::dsc_modelo))

}