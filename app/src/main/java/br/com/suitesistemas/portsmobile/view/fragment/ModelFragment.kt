package br.com.suitesistemas.portsmobile.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.button.hideToBottom
import br.com.suitesistemas.portsmobile.custom.button.showFromBottom
import br.com.suitesistemas.portsmobile.custom.observer.observerHandler
import br.com.suitesistemas.portsmobile.custom.progress_bar.hide
import br.com.suitesistemas.portsmobile.custom.progress_bar.show
import br.com.suitesistemas.portsmobile.custom.recycler_view.*
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.setTitle
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.custom.view.showMessageError
import br.com.suitesistemas.portsmobile.entity.Model
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.ModelFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.ModelSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ModelAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.ModelViewModel
import kotlinx.android.synthetic.main.fragment_model.*

class ModelFragment : BasicFragment<Model, ModelAdapter>(),
    OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,
    Observer<ApiResponse<MutableList<Model>?>> {

    lateinit var viewModel: ModelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ModelViewModel::class.java)
        configureObserver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_model, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(model_layout, ModelAdapter(context!!, viewModel.getSortingList(), {
            delete(it)
        }, {
            edit(viewModel.getBy(it))
        }))
        setTitle(R.string.modelos)
    }

    override fun onPause() {
        super.onPause()
        model_button.hideToBottom()
    }

    override fun onRefresh() {
        when (model_progressbar.isIndeterminate) {
            true -> model_refresh.isRefreshing = false
            false -> refresh()
        }
    }

    private fun refresh() {
        model_progressbar.show()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded(model_progressbar.isIndeterminate, model_layout) {
            val intent = Intent(activity, ModelSearchActivity::class.java)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        viewModel.fetchAllModels(companyName)
        viewModel.response.observe(this, this)
    }

    override fun onChanged(response: ApiResponse<MutableList<Model>?>) {
        model_progressbar.hide()
        model_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded(model_progressbar.isIndeterminate, model_layout) {
                    configureButton()
                    response.data?.let { models ->
                        viewModel.addAll(models)
                        configureList(models)
                    }
                }
            }
        } else {
            showMessageError(model_layout, response.messageError!!, response.operation)
            configureList(viewModel.getSortingList())
        }
    }

    private fun configureButton() {
        with (model_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded(model_progressbar.isIndeterminate, model_layout) {
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
    }

    private fun configureList(models: MutableList<Model>) {
        model_refresh.setOnRefreshListener(this@ModelFragment)
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(models)

        with (model_recyclerview) {
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
            executeAfterLoaded(model_progressbar.isIndeterminate, model_layout) {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        val firebaseToken = FirebaseUtils.getToken(context!!)
        model_progressbar.show()
        viewModel.fetchAllCombinationsBy(position)
        viewModel.modelCombinationResponse.observe(this, observerHandler({
            viewModel.addRemovedModelCombinations(it)
            viewModel.delete(position, firebaseToken)
        }, {
            showMessage(model_layout, getString(R.string.falha_remover_combinacoes))
        }, {
            model_progressbar.hide()
        }))
    }

    override fun deleteRollback() {
        model_progressbar.show()
        viewModel.deleteRollback()
        viewModel.rollbackResponse.observe(this, observerHandler({
            viewModel.add(it, EHttpOperation.ROLLBACK)
            viewModel.removedObject = it
            modelCombinationsDeleteRollback()
        }, {
            model_progressbar.hide()
            showMessage(model_layout, it, getString(R.string.falha_desfazer_acao))
        }))
    }

    private fun modelCombinationsDeleteRollback() {
        viewModel.modelCombinationsDeleteRollback()
        viewModel.modelCombinationRollbackResponse.observe(this, observerHandler({}, {
            showMessage(model_layout, getString(R.string.falha_desfazer_acao))
        }, {
            model_progressbar.hide()
        }))
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded(model_progressbar.isIndeterminate, model_layout) {
            edit(viewModel.getBy(position))
        }
    }

    private fun edit(model: Model?) {
        val intent = Intent(activity, ModelFormActivity::class.java)
        intent.putExtra("model", model)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

}