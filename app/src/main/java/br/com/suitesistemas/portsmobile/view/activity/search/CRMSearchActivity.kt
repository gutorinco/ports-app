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
import br.com.suitesistemas.portsmobile.databinding.ActivityCrmSearchBinding
import br.com.suitesistemas.portsmobile.entity.CRM
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.CRMAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.CRMSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_crm_search.*

class CRMSearchActivity : SearchActivity(),
    OnItemClickListener,
    Observer<ApiResponse<MutableList<CRM>?>> {

    lateinit var viewModel: CRMSearchViewModel
    private lateinit var crmAdapter: CRMAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crm_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(crm_search)
        super.initDatePicker(crm_search_bar_query) { crm_search_bar_query.text = null }
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(CRMSearchViewModel::class.java)
        viewModel.initRepository(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityCrmSearchBinding>(this, R.layout.activity_crm_search)
            .apply {
                lifecycleOwner = this@CRMSearchActivity
                viewModel = this@CRMSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        crm_search_bar_query.setOnClickListener { dateDialog.show() }
        crm_search_bar_home.setOnClickListener { onHomeNavigation() }
        crm_search_bar_done.setOnClickListener(this)
        crm_search_bar.requestFocus()
    }

    private fun configureList(crms: MutableList<CRM>) {
        crmAdapter = CRMAdapter(baseContext, crms, {
            delete(it)
        }, {
            onItemClicked(it, crm_search)
        })
        configureSwipe()
        with (crm_search_recycler_view) {
            adapter = crmAdapter
            addOnItemClickListener(this@CRMSearchActivity)
        }
    }

    private fun configureSwipe() {
        crm_search_recycler_view.addSwipe(SwipeToDeleteCallback(baseContext) { itemPosition ->
            executeAfterLoaded(viewModel.searching.value!!, crm_search) {
                delete(itemPosition)
            }
        })
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, crm_search) {
            hideKeyboard()
            val search = crm_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<CRM>?>) {
        viewModel.searching.value = false
        if (response.messageError == null) {
            onResponse(response.data, response.operation)
        } else {
            showMessageError(crm_search, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun onResponse(data: List<CRM>?, operation: EHttpOperation) {
        when (operation) {
            EHttpOperation.GET -> data?.let { crms -> viewModel.addAll(crms as MutableList<CRM>) }
            EHttpOperation.DELETE -> deleted()
            EHttpOperation.ROLLBACK -> Snackbar.make(crm_search, getString(R.string.acao_desfeita), Snackbar.LENGTH_LONG).show()
            else -> {}
        }
        if (data != null)
            setAdapter(viewModel.completeList)
        else setAdapter(data)
    }

    private fun setAdapter(data: List<CRM>?) = data?.let { crmAdapter.setAdapter(it) }

    private fun delete(position: Int) {
        with (viewModel) {
            val crm = getBy(position)
            val firebaseToken = FirebaseUtils.getToken(this@CRMSearchActivity)
            searching.value = true
            delete(crm.num_codigo_online, position, firebaseToken)
        }
    }

    override fun deleteRollback() {
        with (viewModel) {
            searching.value = true
            deleteRollback("crm")
            rollbackResponse.observe(this@CRMSearchActivity, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
            }, {
                Snackbar
                    .make(crm_search, getString(R.string.falha_desfazer_acao), Snackbar.LENGTH_LONG)
                    .show()
                configureList(getList())
            }, {
                searching.value = false
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        val crm = viewModel.getBy(position)
        val data = Intent()
        data.putExtra("crm_response", crm)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}