package br.com.suitesistemas.portsmobile.view.activity.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.databinding.ActivityModelSearchBinding
import br.com.suitesistemas.portsmobile.entity.Model
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.ModelAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.ModelSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_model_search.*
import kotlinx.android.synthetic.main.fragment_model.*

class ModelSearchActivity : SearchActivity(),
    OnItemClickListener, Observer<ApiResponse<MutableList<Model>?>> {

    lateinit var type: String
    lateinit var viewModel: ModelSearchViewModel
    private lateinit var modelAdapter: ModelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(model_search)
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(ModelSearchViewModel::class.java)
        viewModel.initRepository(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityModelSearchBinding>(this, R.layout.activity_model_search)
            .apply {
                lifecycleOwner = this@ModelSearchActivity
                viewModel = this@ModelSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        model_search_bar_home.setOnClickListener { onHomeNavigation() }
        model_search_bar_done.setOnClickListener(this)
        model_search_bar_query.setOnEditorActionListener(this)
        model_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, model_search) {
            hideKeyboard()
            val search = model_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Model>?>) {
        viewModel.searching.value = false
        if (response.messageError == null) {
            onResponse(response.data, response.operation)
        } else {
            showMessageError(model_search, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun onResponse(data: List<Model>?, operation: EHttpOperation) {
        when (operation) {
            EHttpOperation.GET -> data?.let { models -> viewModel.addAll(models as MutableList<Model>) }
            EHttpOperation.DELETE -> deleted()
            EHttpOperation.ROLLBACK -> Snackbar.make(model_search, getString(R.string.acao_desfeita), Snackbar.LENGTH_LONG).show()
            else -> {}
        }
        if (data != null)
             setAdapter(viewModel.completeList)
        else setAdapter(data)
    }

    private fun setAdapter(data: List<Model>?) = data?.let { modelAdapter.setAdapter(it) }

    private fun configureList(models: MutableList<Model>) {
        modelAdapter = ModelAdapter(baseContext, models, {
            delete(it)
        }, {
            onItemClicked(it, model_search)
        })
        configureSwipe()
        with (model_search_recycler_view) {
            adapter = modelAdapter
            addOnItemClickListener(this@ModelSearchActivity)
        }
    }

    private fun configureSwipe() {
        model_search_recycler_view.addSwipe(SwipeToDeleteCallback(baseContext) { itemPosition ->
            executeAfterLoaded(viewModel.searching.value!!, model_search) {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        viewModel.searching.value = true
        viewModel.fetchAllCombinationsBy(position)
        viewModel.modelCombinationResponse.observe(this,
            observerHandler({
                viewModel.addRemovedModelCombinations(it)
                fetchAllGridCombinations(position)
            }, {
                model_progressbar.hide()
                showMessage(
                    model_layout,
                    getString(R.string.falha_remover_combinacoes)
                )
            })
        )
    }

    private fun fetchAllGridCombinations(position: Int) {
        viewModel.fetchAllGridCombinationsBy(position)
        viewModel.modelGridResponse.observe(this,
            observerHandler({
                val firebaseToken = FirebaseUtils.getToken(this)
                viewModel.addRemovedModelGridCombinations(it)
                val model = viewModel.getBy(position)
                viewModel.delete(model.num_codigo_online, position, firebaseToken)
            }, {
                model_progressbar.hide()
                showMessage(
                    model_layout,
                    getString(R.string.falha_remover_grade)
                )
            })
        )
    }

    override fun deleteRollback() {
        viewModel.searching.value = true
        viewModel.deleteRollback("modelo")
        viewModel.rollbackResponse.observe(this,
            observerHandler({
                viewModel.add(it, EHttpOperation.ROLLBACK)
                viewModel.removedObject = it
                modelCombinationsDeleteRollback()
            }, {
                viewModel.searching.value = false
                showMessage(
                    model_layout,
                    it,
                    getString(R.string.falha_desfazer_acao)
                )
            })
        )
    }

    private fun modelCombinationsDeleteRollback() {
        viewModel.modelCombinationsDeleteRollback()
        viewModel.modelCombinationRollbackResponse.observe(this,
            observerHandler({
                modelGridCombinationsDeleteRollback()
            }, {
                viewModel.searching.value = false
                showMessage(
                    model_layout,
                    getString(R.string.falha_desfazer_acao)
                )
            })
        )
    }

    private fun modelGridCombinationsDeleteRollback() {
        viewModel.modelGridCombinationsDeleteRollback()
        viewModel.modelGridRollbackResponse.observe(this,
            observerHandler({}, {
                showMessage(
                    model_layout,
                    getString(R.string.falha_desfazer_acao)
                )
            }, {
                viewModel.searching.value = false
            })
        )
    }

    override fun onItemClicked(position: Int, view: View) {
        val data = Intent()
        data.putExtra("model_response", viewModel.getBy(position))
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
