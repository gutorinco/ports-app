package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.entity.Grid
import br.com.suitesistemas.portsmobile.entity.Model
import br.com.suitesistemas.portsmobile.entity.ModelCombination
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.service.company.CompanyRepository
import br.com.suitesistemas.portsmobile.service.grid.GridRepository
import br.com.suitesistemas.portsmobile.service.model.ModelRepository
import br.com.suitesistemas.portsmobile.service.model_combination.ModelCombinationRepository

class ModelFormViewModel(application: Application) : FormViewModel<Model>(application) {

    @Bindable
    var model = MutableLiveData<Model>()
    val grids: MutableList<Grid> = mutableListOf()
    val combinations: MutableList<Combination> = mutableListOf()
    var removedCombination: Combination? = null
    val removedCombinations: MutableList<Combination> = mutableListOf()
    val modelCombinations: MutableList<ModelCombination> = mutableListOf()
    val newModelCombinations: MutableList<ModelCombination> = mutableListOf()
    var gridsResponse = MutableLiveData<ApiResponse<MutableList<Grid>?>>()
    var modelCombinationResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    var modelCombinationInsertResponse = MutableLiveData<ApiResponse<MutableList<ModelCombination>?>>()
    var modelCombinationDeleteResponse = MutableLiveData<ApiResponse<Boolean?>>()
    private lateinit var modelRepository: ModelRepository
    private lateinit var modelCombinationRepository: ModelCombinationRepository
    private lateinit var gridRepository: GridRepository

    init {
        model.value = Model()
    }

    fun initRepositories(companyName: String) {
        modelRepository = ModelRepository(companyName)
        companyRepository  = CompanyRepository(companyName)
        gridRepository  = GridRepository(companyName)
        modelCombinationRepository  = ModelCombinationRepository(companyName)
    }

    fun fetchAllGrids() {
        gridsResponse = when (grids.isNullOrEmpty()) {
            true -> gridRepository.findAll()
            false -> getApiResponseFromExistList(grids)
        }
    }

    fun fetchAllCombinations() {
        modelCombinationResponse = when (modelCombinations.isNullOrEmpty()) {
            true -> modelCombinationRepository.findBy(model.value?.num_codigo_online!!)
            false -> getApiResponseFromExistList(modelCombinations)
        }
    }

    fun addAllGrids(grids: MutableList<Grid>) {
        this.grids.addAll(grids)
    }

    fun addAllModelCombinations(modelCombinations: MutableList<ModelCombination>) {
        this.modelCombinations.addAll(modelCombinations)
        combinations.addAll(modelCombinations.map { it.cod_combinacao })
    }

    fun concat(modelToConcat: Model) {
        model.value = modelToConcat
    }

    fun validateForm(gridPosition: Int, companyPosition: Int) {
        val model = Model(this.model.value!!)

        if (grids.isNullOrEmpty())
            throw InvalidValueException(getStringRes(R.string.nenhuma_grade))
        if (combinations.isEmpty())
            throw InvalidValueException(getStringRes(R.string.adicione_combinacoes))

        model.fky_empresa = companies[companyPosition]
        model.fky_grade = grids[gridPosition]

        if (model.dsc_modelo.isEmpty())
            throw InvalidValueException("Nome", getStringRes(R.string.obrigatorio))

        this.model.value = Model(model)
    }

    fun save(firebaseToken: String) {
        if (model.value?.num_codigo_online.isNullOrEmpty())
            insert(firebaseToken)
        else update(firebaseToken)
    }

    private fun insert(firebaseToken: String) {
        insertResponse = modelRepository.insert(getJsonRequest("modelo", model.value!!, firebaseToken))
    }

    private fun update(firebaseToken: String) {
        updateResponse = modelRepository.update(getJsonRequest("modelo", model.value!!, firebaseToken))
    }

    fun insertCombinations() {
        newModelCombinations.forEach { it.cod_modelo = model.value!! }
        modelCombinationInsertResponse = modelCombinationRepository.insert(newModelCombinations)
    }

    fun deleteCombinations() {
        removedCombination = removedCombinations.first()
        modelCombinationDeleteResponse = modelCombinationRepository.delete(model.value?.num_codigo_online!!, removedCombination!!.cod_combinacao)
    }

    fun removeCombinationBy(position: Int) {
        val combination = combinations[position]
        removedCombinations.add(combination)
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