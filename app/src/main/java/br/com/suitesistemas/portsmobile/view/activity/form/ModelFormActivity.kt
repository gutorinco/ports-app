package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.button.showFromBottom
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.observer.observerHandler
import br.com.suitesistemas.portsmobile.custom.progress_bar.hide
import br.com.suitesistemas.portsmobile.custom.progress_bar.show
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.custom.recycler_view.addSwipe
import br.com.suitesistemas.portsmobile.custom.spinner.setAdapterAndSelection
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.hideKeyboard
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.databinding.ActivityModelFormBinding
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.entity.Company
import br.com.suitesistemas.portsmobile.entity.Grid
import br.com.suitesistemas.portsmobile.entity.Model
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.SelectCombinationSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.CombinationAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.ModelFormViewModel
import kotlinx.android.synthetic.main.activity_model_form.*

class ModelFormActivity : FormActivity() {

    private lateinit var combinationAdapter: CombinationAdapter
    private lateinit var viewModel: ModelFormViewModel
    companion object {
        private const val COMBINATIONS_SELECTED = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_form)

        configureActionBar(R.string.cadastro_modelo)
        initViewModel()
        configureObservers()
        configureDataBinding()
        configureList()
        configureButtons()
        val modelToEdit = intent.getParcelableExtra<Model>("model")
        if (modelToEdit != null)
            edit(modelToEdit)
    }

    override fun getBtnConfirm() = model_form_btn_confirm

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(ModelFormViewModel::class.java)
        viewModel.initRepositories(companyName)
        viewModel.fetchAllCompanies()
        viewModel.fetchAllGrids()
    }

    private fun configureObservers() {
        viewModel.companiesResponse.observe(this, observerHandler({
            configureCompanyAdapter(it)
        },{
            showMessage(model_form, it, getString(R.string.nao_encontrou_empresas))
        }))

        viewModel.gridsResponse.observe(this, observerHandler({
            configureGridAdapter(it)
        }, {
            showMessage(model_form, it, getString(R.string.nao_encontrou_combinacoes))
        }, {
            model_form_progressbar.hide()
        }))
    }

    private fun configureCompanyAdapter(companies: MutableList<Company>) {
        viewModel.addAllCompanies(companies)
        val companiesName = companies.map { company -> company.dsc_empresa }
        val index = companies.indexOfFirst { company ->
            company.cod_empresa == viewModel.model.value?.fky_empresa?.cod_empresa
        }
        model_form_company.setAdapterAndSelection(this, companiesName, index)
    }

    private fun configureGridAdapter(grids: MutableList<Grid>) {
        viewModel.addAllGrids(grids)
        val gridsName = grids.map { grid -> grid.dsc_grade }
        val index = grids.indexOfFirst { company ->
            company.cod_grade == viewModel.model.value?.fky_grade?.cod_grade
        }
        model_form_grid.setAdapterAndSelection(this, gridsName, index)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityModelFormBinding>(this, R.layout.activity_model_form)
            .apply {
                lifecycleOwner = this@ModelFormActivity
                viewModel = this@ModelFormActivity.viewModel
            }
    }

    private fun configureList() {
        combinationAdapter = CombinationAdapter(baseContext, viewModel.combinations)
        model_form_combinations.adapter = combinationAdapter
        configureSwipe()
    }

    private fun configureSwipe() {
        model_form_combinations.addSwipe(SwipeToDeleteCallback(baseContext) { position ->
            viewModel.removeCombinationBy(position)
            combinationAdapter.notifyDataSetChanged()
        })
    }

    private fun configureButtons() {
        model_form_btn_confirm.setOnClickListener {
            executeAfterLoaded(model_form_progressbar.isIndeterminate, model_form) {
                onConfirm()
            }
        }
        model_form_btn_combination.setOnClickListener {
            executeAfterLoaded(model_form_progressbar.isIndeterminate, model_form) {
                val intent = Intent(this, SelectCombinationSearchActivity::class.java)
                startActivityForResult(intent, COMBINATIONS_SELECTED)
            }
        }
    }

    private fun onConfirm() {
        hideKeyboard()
        model_form_name.error = null

        try {
            val gridPosition = model_form_grid.selectedItemPosition
            val companyPosition = model_form_company.selectedItemPosition

            viewModel.model.value?.dbl_preco_unit_vista = getDoubleValueFrom(model_form_price)
            viewModel.model.value?.dbl_preco_unit_prazo = getDoubleValueFrom(model_form_price_financed)
            viewModel.validateForm(gridPosition, companyPosition)

            model_form_progressbar.show()
            val firebaseToken = FirebaseUtils.getToken(this)

            viewModel.save(firebaseToken)
            configureInsertObserver()
            configureUpdateObserver()
        } catch (ex: InvalidValueException) {
            when (ex.field) {
                "Nome" -> model_form_name.error = ex.message
                else -> showMessage(model_form, ex.message!!)
            }
        }
    }

    private fun configureInsertObserver() {
        viewModel.insertResponse.observe(this, observerHandler({
            viewModel.model.value = it
            saveCombinations()
        }, {
            showMessage(model_form, it, getString(R.string.falha_inserir_modelo))
        }))
    }

    private fun configureUpdateObserver() {
        viewModel.updateResponse.observe(this, observerHandler({
            viewModel.model.value?.version = it.version
            saveCombinations()
        }, {
            showMessage(model_form, it, getString(R.string.falha_atualizar_modelo))
        }))
    }

    private fun edit(modelToEdit: Model) {
        model_form_progressbar.show()
        viewModel.concat(modelToEdit)
        viewModel.fetchAllCombinations()
        viewModel.modelCombinationResponse.observe(this, observerHandler({
            viewModel.addAllModelCombinations(it)
            configureList()
        }, {
            showMessage(model_form, it, getString(R.string.nao_encontrou_combinacoes))
        }, {
            model_form_progressbar.hide()
        }))
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

    override fun onPostResume() {
        super.onPostResume()
        model_form_btn_confirm.showFromBottom()
    }

    private fun saveCombinations() {
        if (viewModel.newModelCombinations.isNotEmpty()) {
            viewModel.insertCombinations()
            viewModel.modelCombinationInsertResponse.observe(this, observerHandler({
                viewModel.newModelCombinations.clear()
                deleteCombinations()
            }, {
                model_form_progressbar.hide()
                showMessage(model_form, it, getString(R.string.falha_inserir_combinacoes))
            }))
        } else {
            deleteCombinations()
        }
    }

    private fun deleteCombinations() {
        if (viewModel.removedCombinations.isNotEmpty()) {
            model_form_progressbar.show()
            viewModel.deleteCombinations()
            viewModel.modelCombinationDeleteResponse.observe(this, observerHandler({
                viewModel.removedCombinations.removeAt(0)
                deleteCombinations()
            }, {
                showMessage(model_form, it, getString(R.string.falha_remover_combinacoes))
            }))
        } else {
            model_form_progressbar.hide()
            created(viewModel.model.value!!)
        }
    }

    private fun created(model: Model) {
        val data = Intent()
        data.putExtra("model_response", model)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
