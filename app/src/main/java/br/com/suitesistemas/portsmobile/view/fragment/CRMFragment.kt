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
import br.com.suitesistemas.portsmobile.entity.CRM
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.CRMFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.CRMSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.CRMAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.CRMViewModel
import kotlinx.android.synthetic.main.fragment_crm.*

class CRMFragment : BasicFragment<CRM, CRMAdapter>(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<CRM>?>> {

    lateinit var viewModel: CRMViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CRMViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crm, container, false)
        setHasOptionsMenu(true)
        configureObserver()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(crm_layout, CRMAdapter(context!!, viewModel.getList(), {
            delete(it)
        }, {
            edit(viewModel.getBy(it), UPDATE_REQUEST_CODE)
        }))
        setTitle(R.string.crm)
    }

    override fun getFloatingButton() = crm_button
    override fun getProgressBar() = crm_progressbar
    override fun getRefresh() = crm_refresh
    override fun getLayout() = crm_layout

    override fun refresh() {
        showProgress()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded {
            val intent = Intent(activity, CRMSearchActivity::class.java)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        with (viewModel) {
            initRepositories(companyName)
            fetchAll()
            response.observe(this@CRMFragment, this@CRMFragment)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<CRM>?>) {
        hideProgress()
        crm_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded {
                    configureButton()
                    response.data?.let { crms ->
                        viewModel.addAll(crms)
                        configureList(crms)
                    }
                }
            }
        } else {
            showMessageError(crm_layout, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun configureButton() {
        with (crm_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded {
                    showProgress()
                    val intent = Intent(activity, CRMFormActivity::class.java)
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
                        val crmResponse = it.getParcelableExtra("crm_response") as CRM
                        viewModel.add(crmResponse)
                    }
                    UPDATE_REQUEST_CODE -> {
                        val crmResponse = it.getParcelableExtra("crm_response") as CRM
                        viewModel.updateList(crmResponse)
                    }
                    GET_REQUEST_CODE -> {
                        val crmResponse = it.getParcelableExtra("crm_response") as CRM
                        edit(crmResponse, GET_REQUEST_CODE)
                    }
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
        crm_button.showFromBottom()
        hideProgress()
    }

    private fun configureList(crms: MutableList<CRM>) {
        crm_refresh.setOnRefreshListener(this@CRMFragment)
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(crms)

        with (crm_recyclerview) {
            adapter = customAdapter
            addOnItemClickListener(this@CRMFragment)
            hideButtonOnScroll(crm_button)
        }
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            crm_empty_view.visibility = View.VISIBLE
            crm_recyclerview.visibility = View.GONE
        } else {
            crm_empty_view.visibility = View.GONE
            crm_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun configureSwipe() {
        crm_recyclerview.addSwipe(SwipeToDeleteCallback(context!!) { itemPosition ->
            executeAfterLoaded {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        showProgress()
        viewModel.delete(position, getFirebaseToken())
    }

    override fun deleteRollback() {
        showProgress()
        with (viewModel) {
            deleteRollback()
            rollbackResponse.observe(this@CRMFragment, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
                removedObject = it
            }, {
                hideProgress()
                showMessage(it, R.string.falha_desfazer_acao)
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded {
            edit(viewModel.getBy(position), UPDATE_REQUEST_CODE)
        }
    }

    private fun edit(crm: CRM, requestCode: Int) {
        showProgress()
        val intent = Intent(activity, CRMFormActivity::class.java)
        intent.putExtra("crm", crm)
        startActivityForResult(intent, requestCode)
    }

}
