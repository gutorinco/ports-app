package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.databinding.ActivityModelFormBinding
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.entity.Model
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.SelectCombinationSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.CombinationAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.ModelFormViewModel
import kotlinx.android.synthetic.main.activity_model_form.*

class ModelFormActivity : FormActivity<Model>(R.menu.menu_product_form, R.id.menu_search_colors) {

    private lateinit var combinationAdapter: CombinationAdapter
    private lateinit var viewModel: ModelFormViewModel
    private val thisActivity = this@ModelFormActivity
    companion object {
        private const val COMBINATIONS_SELECTED = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_form)

        configureActionBar(R.string.cadastro_modelo)
        initViewModel()
        configureDataBinding()
    }

    override fun onResume() {
        super.onResume()
        fetchCompanies()
        fetchGrids()
        configureForm()
        configureList()
        configureBtnConfirm()
        val modelToEdit = intent.getParcelableExtra<Model>("model")
        if (modelToEdit != null)
            edit(modelToEdit)
    }

    override fun getBtnConfirm() = model_form_btn_confirm
    override fun getProgressBar() = model_form_progressbar
    override fun getLayout() = model_form

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(ModelFormViewModel::class.java)
        viewModel.initRepositories(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityModelFormBinding>(this, R.layout.activity_model_form)
            .apply {
                lifecycleOwner = this@ModelFormActivity
                viewModel = this@ModelFormActivity.viewModel
                doubleUtils = DoubleUtils()
            }
    }

    private fun fetchCompanies() {
        with (viewModel) {
            fetchAllCompanies()
            companiesResponse.observe(this@ModelFormActivity, observerHandler({
                addAllCompanies(it)
                val companiesName = getCompaniesNames()
                val index = getCompanyIndex()
                model_form_company.setAdapterAndSelection(this@ModelFormActivity, companiesName, index)
            }, {
                showMessage(it, R.string.nao_encontrou_empresas)
            }))
        }
    }

    private fun fetchGrids() {
        with (viewModel) {
            fetchAllGrids()
            gridsResponse.observe(this@ModelFormActivity, observerHandler({
                addAllGrids(it)
                val gridsName = getGridsNames()
                val index = getGridIndex()
                model_form_grid.setAdapterAndSelection(this@ModelFormActivity, gridsName, index)
            }, {
                showMessage(it, R.string.nao_encontrou_combinacoes)
            }, {
                hideProgress()
            }))
        }
    }

    private fun configureForm() {
        model_form_price.addNumberMask()
        model_form_price_financed.addNumberMask()
        model_form_company.onItemSelected { viewModel.model.value!!.fky_empresa = viewModel.companies[it] }
        model_form_grid.onItemSelected { viewModel.model.value!!.fky_grade = viewModel.grids[it] }
    }

    private fun configureList() {
        combinationAdapter = CombinationAdapter(baseContext, viewModel.combinations) { onRemove(it) }
        with (model_form_combinations) {
            adapter = combinationAdapter
            addSwipe(SwipeToDeleteCallback(baseContext) { onRemove(it) })
        }
        configureEmptyView()
    }

    private fun onRemove(position: Int) {
        with (viewModel) {
            removeCombinationBy(position)
            combinationAdapter.notifyDataSetChanged()
            if (listIsEmpty())
                configureEmptyView()
        }
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            model_form_empty_view.visibility = View.VISIBLE
            model_form_combinations.visibility = View.GONE
        } else {
            model_form_empty_view.visibility = View.GONE
            model_form_combinations.visibility = View.VISIBLE
        }
    }

    private fun configureBtnConfirm() {
        model_form_btn_confirm.setOnClickListener {
            executeAfterLoaded { onConfirm() }
        }
    }

    private fun onConfirm() {
        hideKeyboard()
        model_form_name.error = null

        try {
            with (viewModel.model.value!!) {
                dbl_preco_unit_vista = model_form_price.toDoubleValue()
                dbl_preco_unit_prazo = model_form_price_financed.toDoubleValue()
                viewModel.validateForm()
                fetchGridItemsBy(fky_grade.cod_grade)
            }
        } catch (ex: InvalidValueException) {
            when (ex.field) {
                "Nome" -> model_form_name.error = ex.message
                else -> showMessage(ex.message!!)
            }
        }
    }

    override fun onClickedMenu() {
        executeAfterLoaded {
            val intent = Intent(this, SelectCombinationSearchActivity::class.java)
            startActivityForResult(intent, COMBINATIONS_SELECTED)
        }
    }

    private fun fetchGridItemsBy(gridId: Int) {
        showProgress()
        val firebaseToken = FirebaseUtils.getToken(this)

        with (viewModel) {
            fetchAllGridItems(gridId)
            gridItemsResponse.observe(thisActivity, observerHandler({
                with(gridItems) {
                    clear()
                    addAll(it)
                }
                if (model.value?.num_codigo_online.isNullOrEmpty())
                     thisActivity.insert(firebaseToken)
                else thisActivity.update(firebaseToken)
            }, {
                handleError(it, R.string.nao_encontrou_grade_modelo)
            }))
        }
    }

    private fun insert(firebaseToken: String) {
        with (viewModel) {
            insert(firebaseToken)
            insertResponse.observe(thisActivity, observerHandler({
                model.value = it
                thisActivity.insertCombinations()
            }, {
                showMessage(it, R.string.falha_inserir_modelo)
            }))
        }
    }

    private fun update(firebaseToken: String) {
        with (viewModel) {
            update(firebaseToken)
            updateResponse.observe(thisActivity, observerHandler({
                model.value?.version = it.version
                thisActivity.insertCombinations()
            }, {
                showMessage(it, R.string.falha_atualizar_modelo)
            }))
        }
    }

    private fun insertCombinations() {
        with (viewModel) {
            if (newModelCombinations.isNotEmpty()) {
                insertCombinations()
                modelCombinationInsertResponse.observe(thisActivity, observerHandler({
                    Handler().postDelayed({ saveGrids() }, 250)
                }, {
                    handleError(it, R.string.falha_inserir_combinacoes)
                }))
            } else {
                thisActivity.deleteGrids()
            }
        }
    }

    private fun saveGrids() {
        with (viewModel) {
            buildGrid()
            insertGrids()
            modelGridInsertResponse.observe(thisActivity, observerHandler({
                thisActivity.deleteGrids()
            }, {
                handleError(it, R.string.falha_inserir_grade)
            }))
        }
    }

    private fun deleteGrids() {
        with (viewModel) {
            if (removedGrids.isNotEmpty()) {
                deleteGrids()
                modelGridDeleteResponse.observe(thisActivity, observerHandler({
                    removedGrids.removeAt(0)
                    thisActivity.deleteGrids()
                }, {
                    handleError(it, R.string.falha_remover_grade)
                }))
            } else {
                thisActivity.deleteCombinations()
            }
        }
    }

    private fun deleteCombinations() {
        with (viewModel) {
            if (removedCombinations.isNotEmpty()) {
                deleteCombinations()
                modelCombinationDeleteResponse.observe(thisActivity, observerHandler({
                    removedCombinations.removeAt(0)
                    thisActivity.deleteCombinations()
                }, {
                    handleError(it, R.string.falha_remover_combinacoes)
                }))
            } else {
                created("model_response", model.value!!)
            }
        }
    }

    override fun handleError(errorMessage: String, clientMessage: Int) {
        super.handleError(errorMessage, clientMessage)
        delete()
    }

    private fun delete() {
        val firebaseToken = FirebaseUtils.getToken(this)
        with (viewModel) {
            delete(firebaseToken)
            deleteResponse.observe(thisActivity, observerHandler({
                model.value!!.num_codigo_online = ""
            }, {
                showMessage(it, R.string.nao_excluiu)
            }))
        }
    }

    private fun edit(modelToEdit: Model) {
        showProgress()
        viewModel.concat(modelToEdit)
        fetchCombinations()
        fetchGridCombinations()
    }

    private fun fetchCombinations() {
        with (viewModel) {
            fetchAllCombinations()
            modelCombinationResponse.observe(thisActivity, observerHandler({
                addAllModelCombinations(it)
                configureList()
            }, {
                showMessage(it, R.string.nao_encontrou_combinacoes)
            }, {
                hideProgress()
            }))
        }
    }

    private fun fetchGridCombinations() {
        with (viewModel) {
            fetchAllGridCombinations()
            modelGridResponse.observe(thisActivity, observerHandler({
                modelGrids.addAll(it)
            }, {
                showMessage(it, R.string.nao_encontrou_grade_modelo)
            }, {
                hideProgress()
            }))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == COMBINATIONS_SELECTED) {
            if (resultCode == Activity.RESULT_OK) {
                data?.extras?.let { bundle ->
                    val combinationsSelected = bundle.getParcelableArrayList<Combination>("combinations_selected")
                    viewModel.addSelectedCombinations(combinationsSelected)
                    configureList()
                }
            }
        }
        hideKeyboard()
    }

}
