package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.*
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository
import br.com.suitesistemas.portsmobile.service.grid.GridRepository
import br.com.suitesistemas.portsmobile.service.grid.item.GridItemRepository
import br.com.suitesistemas.portsmobile.service.model.ModelRepository
import br.com.suitesistemas.portsmobile.service.model_combination.ModelCombinationRepository
import br.com.suitesistemas.portsmobile.service.model_grid_combination.ModelGridCombinationRepository

class ModelFormViewModel(application: Application) : FormViewModel<Model>(application) {

    @Bindable
    var model = MutableLiveData<Model>()
    val grids: MutableList<Grid> = mutableListOf()
    val gridItems: MutableList<GridItem> = mutableListOf()
    val combinations: MutableList<Combination> = mutableListOf()
    var modelGrid: ModelGridCombination = ModelGridCombination()
    val modelGrids: MutableList<ModelGridCombination> = mutableListOf()
    val newModelGrids: MutableList<ModelGridCombination> = mutableListOf()
    var removedCombination: Combination? = null
    val removedCombinations: MutableList<Combination> = mutableListOf()
    val removedGrids: MutableList<ModelGridCombination> = mutableListOf()
    val modelCombinations: MutableList<ModelCombination> = mutableListOf()
    val newModelCombinations: MutableList<ModelCombination> = mutableListOf()
    var deleteResponse = MutableLiveData<ApiResponse<Boolean?>>()
    var gridsResponse = MutableLiveData<ApiResponse<MutableList<Grid>?>>()
    var gridItemsResponse = MutableLiveData<ApiResponse<MutableList<GridItem>?>>()
    var modelCombinationResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    var modelCombinationInsertResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    var modelCombinationDeleteResponse = MutableLiveData<ApiResponse<Boolean?>>()
    var modelGridResponse = MutableLiveData<ApiResponse<MutableList<ModelGridCombination>?>>()
    var modelGridInsertResponse = MutableLiveData<ApiResponse<MutableList<ModelGridCombination>?>>()
    var modelGridDeleteResponse = MutableLiveData<ApiResponse<Boolean?>>()
    private lateinit var modelRepository: ModelRepository
    private lateinit var modelGridRepository: ModelGridCombinationRepository
    private lateinit var modelCombinationRepository: ModelCombinationRepository
    private lateinit var gridRepository: GridRepository
    private lateinit var gridItemRepository: GridItemRepository

    init {
        model.value = Model()
    }

    fun initRepositories(companyName: String) {
        modelRepository = ModelRepository(companyName)
        companyRepository = CompanyRepository(companyName)
        gridItemRepository = GridItemRepository(companyName)
        gridRepository = GridRepository(companyName)
        modelGridRepository = ModelGridCombinationRepository(companyName)
        modelCombinationRepository = ModelCombinationRepository(companyName)
    }

    fun fetchAllGrids() {
        gridsResponse = when (grids.isNullOrEmpty()) {
            true -> gridRepository.findAll()
            false -> getApiResponseFromExistList(grids)
        }
    }

    fun fetchAllGridItems(gridId: Int) {
        gridItemsResponse = gridItemRepository.findAllBy(gridId)
    }

    fun fetchAllCombinations() {
        modelCombinationResponse = when (modelCombinations.isNullOrEmpty()) {
            true -> modelCombinationRepository.findBy(model.value?.num_codigo_online!!)
            false -> getApiResponseFromExistList(modelCombinations)
        }
    }

    fun fetchAllGridCombinations() {
        modelGridResponse = when (modelGrids.isNullOrEmpty()) {
            true -> modelGridRepository.findBy(model.value?.num_codigo_online!!)
            false -> getApiResponseFromExistList(modelGrids)
        }
    }

    fun getGridsNames() = grids.map { grid -> grid.dsc_grade }

    fun getGridIndex(): Int = grids.indexOfFirst { company ->
        company.cod_grade == model.value?.fky_grade?.cod_grade
    }

    override fun getCompanyIndex(): Int = companies.indexOfFirst { company ->
        company.cod_empresa == model.value?.fky_empresa?.cod_empresa
    }

    fun addAllGrids(grids: MutableList<Grid>) {
        this.grids.addAll(grids)
    }

    fun addAllModelCombinations(modelCombinations: MutableList<ModelCombination>) {
        this.modelCombinations.addAll(modelCombinations)
        combinations.addAll(modelCombinations.map { it.cod_combinacao })
    }

    fun listIsEmpty() = combinations.isNullOrEmpty()

    fun concat(modelToConcat: Model) {
        model.value = modelToConcat
    }

    fun validateForm() {
        val model = Model(this.model.value!!)

        if (grids.isNullOrEmpty())
            throw InvalidValueException(getStringRes(R.string.nenhuma_grade))
        if (combinations.isEmpty())
            throw InvalidValueException(getStringRes(R.string.adicione_combinacoes))
        if (model.dsc_modelo.isEmpty())
            throw InvalidValueException("Nome", getStringRes(R.string.obrigatorio))

        this.model.value = Model(model)
    }

    fun buildGrid() {
        newModelGrids.clear()
        newModelCombinations.forEach {
            gridItems.forEach { item ->
                newModelGrids.add(ModelGridCombination(model.value!!, it.cod_combinacao, item))
            }
        }
        newModelCombinations.clear()
    }

    fun insert(firebaseToken: String) {
        insertResponse = modelRepository.insert(getJsonRequest("modelo", model.value!!, firebaseToken))
    }

    fun update(firebaseToken: String) {
        updateResponse = modelRepository.update(getJsonRequest("modelo", model.value!!, firebaseToken))
    }

    fun delete(firebaseToken: String) {
        deleteResponse = modelRepository.delete(model.value!!.num_codigo_online, firebaseToken)
    }

    fun insertCombinations() {
        newModelCombinations.forEach { it.cod_modelo = model.value!! }
        modelCombinationInsertResponse = modelCombinationRepository.insert(newModelCombinations)
    }

    fun deleteCombinations() {
        removedCombination = removedCombinations.first()
        modelCombinationDeleteResponse = modelCombinationRepository.delete(model.value?.num_codigo_online!!, removedCombination!!.cod_combinacao)
    }

    fun insertGrids() {
        modelGridInsertResponse = modelGridRepository.insert(newModelGrids)
    }

    fun deleteGrids() {
        modelGrid = removedGrids.first()
        modelGridDeleteResponse = modelGridRepository.delete(model.value?.num_codigo_online!!, modelGrid.ids.cod_combinacao)
    }

    fun removeCombinationBy(position: Int) {
        val combination = combinations[position]
        removedCombinations.add(combination)
        modelGrids
            .filter { it.ids.cod_combinacao == combination.cod_combinacao }
            .forEach { removedGrids.add(it) }
        combinations.remove(combination)
    }

    fun addSelectedCombinations(combinationsSelected: List<Combination>?) {
        combinationsSelected?.map {
            if (combinations.find { c -> c.cod_combinacao == it.cod_combinacao } == null) {
                newModelCombinations.add(ModelCombination(model.value!!, it))
                combinations.add(it)
            }
        }
    }

}