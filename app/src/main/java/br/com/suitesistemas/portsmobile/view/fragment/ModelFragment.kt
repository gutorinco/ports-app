package br.com.suitesistemas.portsmobile.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.entity.Model
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.ModelFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.ModelSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ModelAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.ModelViewModel
import kotlinx.android.synthetic.main.fragment_model.*

class ModelFragment : BasicFragment<Model, ModelAdapter>(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<Model>?>> {

    lateinit var viewModel: ModelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ModelViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_model, container, false)
        setHasOptionsMenu(true)
        configureObserver()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(model_layout, ModelAdapter(context!!, viewModel.getList(), {
            delete(it)
        }, {
            edit(viewModel.getBy(it))
        }))
        setTitle(R.string.modelos)
    }

    override fun getFloatingButton() = model_button
    override fun getProgressBar() = model_progressbar
    override fun getRefresh() = model_refresh
    override fun getLayout() = model_layout

    override fun refresh() {
        showProgress()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded {
            val intent = Intent(activity, ModelSearchActivity::class.java)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        with(viewModel) {
            initRepositories(companyName)
            fetchAll()
            response.observe(this@ModelFragment, this@ModelFragment)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Model>?>) {
        hideProgress()
        model_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded {
                    configureButton()
                    response.data?.let { models ->
                        viewModel.addAll(models)
                        configureList(models)
                    }
                }
            }
        } else {
            showMessageError(model_layout, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun configureButton() {
        with(model_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded {
                    val intent = Intent(activity, ModelFormActivity::class.java)
                    startActivityForResult(intent, CREATE_REQUEST_CODE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                when (requestCode) {
                    CREATE_REQUEST_CODE -> {
                        val modelResponse = it.getParcelableExtra("model_response") as Model
                        viewModel.add(modelResponse)
                    }
                    UPDATE_REQUEST_CODE -> {
                        val modelResponse = it.getParcelableExtra("model_response") as Model
                        viewModel.updateList(modelResponse)
                    }
                    GET_REQUEST_CODE -> {
                        val modelResponse = it.getParcelableExtra("model_response") as Model
                        edit(modelResponse)
                    }
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
        model_button.showFromBottom()
        hideProgress()
    }

    private fun configureList(models: MutableList<Model>) {
        model_refresh.setOnRefreshListener(this@ModelFragment)
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(models)

        with(model_recyclerview) {
            adapter = customAdapter
            addOnItemClickListener(this@ModelFragment)
            hideButtonOnScroll(model_button)
        }
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            model_empty_view.visibility = View.VISIBLE
            model_recyclerview.visibility = View.GONE
        } else {
            model_empty_view.visibility = View.GONE
            model_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun configureSwipe() {
        model_recyclerview.addSwipe(SwipeToDeleteCallback(context!!) { itemPosition ->
            executeAfterLoaded {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        showProgress()
        with (viewModel) {
            fetchAllCombinationsBy(position)
            modelCombinationResponse.observe(this@ModelFragment, observerHandler({
                addRemovedModelCombinations(it)
                fetchAllGridCombinations(position)
            }, {
                handleError(it, R.string.falha_remover_combinacoes)
            }))
        }
    }

    private fun fetchAllGridCombinations(position: Int) {
        with (viewModel) {
            fetchAllGridCombinationsBy(position)
            modelGridResponse.observe(this@ModelFragment, observerHandler({
                addRemovedModelGridCombinations(it)
                delete(position, getFirebaseToken())
            }, {
                handleError(it, R.string.falha_remover_grade)
            }))
        }
    }

    override fun deleteRollback() {
        showProgress()
        with (viewModel) {
            deleteRollback()
            rollbackResponse.observe(this@ModelFragment, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
                removedObject = it
                this@ModelFragment.modelCombinationsDeleteRollback()
            }, {
                handleError(it, R.string.falha_desfazer_acao)
            }))
        }
    }

    private fun modelCombinationsDeleteRollback() {
        with (viewModel) {
            modelCombinationsDeleteRollback()
            modelCombinationRollbackResponse.observe(this@ModelFragment, observerHandler({
                this@ModelFragment.modelGridCombinationsDeleteRollback()
            }, {
                handleError(it, R.string.falha_desfazer_acao)
            }))
        }
    }

    private fun modelGridCombinationsDeleteRollback() {
        with (viewModel) {
            modelGridCombinationsDeleteRollback()
            modelGridRollbackResponse.observe(this@ModelFragment, observerHandler({}, {
                showMessage(getString(R.string.falha_desfazer_acao))
            }, {
                hideProgress()
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded {
            edit(viewModel.getBy(position))
        }
    }

    private fun edit(model: Model?) {
        showProgress()
        val intent = Intent(activity, ModelFormActivity::class.java)
        intent.putExtra("model", model)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

}