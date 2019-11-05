package br.com.suitesistemas.portsmobile.view.activity.search

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.extensions.hideKeyboard
import br.com.suitesistemas.portsmobile.custom.extensions.showMessageError
import br.com.suitesistemas.portsmobile.databinding.ActivityFinancialReleaseSearchBinding
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.FinancialReleaseAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.FinancialReleaseSearchViewModel
import kotlinx.android.synthetic.main.activity_financial_release_search.*

class FinancialReleaseSearchActivity : SearchActivity(), Observer<ApiResponse<MutableList<FinancialRelease>?>> {

    lateinit var viewModel: FinancialReleaseSearchViewModel
    private lateinit var releaseAdapter: FinancialReleaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_release_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(financial_search)
        super.initDatePicker(financial_search_bar_query) { financial_search_bar_query.text = null }
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(FinancialReleaseSearchViewModel::class.java)
        viewModel.initRepository(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityFinancialReleaseSearchBinding>(this, R.layout.activity_financial_release_search)
            .apply {
                lifecycleOwner = this@FinancialReleaseSearchActivity
                viewModel = this@FinancialReleaseSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        financial_search_bar_home.setOnClickListener { this@FinancialReleaseSearchActivity.onBackPressed() }
        financial_search_bar_done.setOnClickListener(this)
        financial_search_bar_query.setOnClickListener { dateDialog.show() }
        financial_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, financial_search) {
            hideKeyboard()
            val search = financial_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<FinancialRelease>?>) {
        if (response.messageError == null) {
            response.data?.let {
                viewModel.addAll(it)
                releaseAdapter.setAdapter(it)
            }
        } else {
            showMessageError(
                financial_search,
                "FINANCIAL SEARCH ERROR:",
                response.messageError,
                getString(R.string.nenhum_resultado)
            )
        }

        viewModel.searching.value = false
    }

    private fun configureList(releases: MutableList<FinancialRelease>) {
        releaseAdapter = FinancialReleaseAdapter(baseContext, releases)
        financial_search_recycler_view.adapter = releaseAdapter
    }

    override fun deleteRollback() = TODO("not implemented")

}
