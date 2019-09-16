package br.com.suitesistemas.portsmobile.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.progress_bar.hide
import br.com.suitesistemas.portsmobile.custom.progress_bar.show
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.setTitle
import br.com.suitesistemas.portsmobile.entity.FinancialRelease
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.FinancialReleaseSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.FinancialReleaseAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.FinancialReleaseViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_financial.*

class FinancialFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, Observer<ApiResponse<MutableList<FinancialRelease>?>> {

    private lateinit var viewModel: FinancialReleaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FinancialReleaseViewModel::class.java)
        configureObserver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_financial, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTitle(R.string.lancamentos)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search_action -> {
                initSearchFinancialReleaseActivity()
                super.onOptionsItemSelected(item)
            }
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initSearchFinancialReleaseActivity() {
        executeAfterLoaded(financial_progressbar.isIndeterminate, financial_layout) {
            val intent = Intent(activity, FinancialReleaseSearchActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRefresh() {
        when (financial_progressbar.isIndeterminate) {
            true -> financial_refresh.isRefreshing = false
            false -> refresh()
        }
    }

    private fun refresh() {
        financial_progressbar.show()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        viewModel.fetchAllFinancialReleases(companyName)
        viewModel.response.observe(this, this)
    }

    override fun onChanged(response: ApiResponse<MutableList<FinancialRelease>?>) {
        financial_progressbar.hide()
        financial_refresh.isRefreshing = false

        if (response.messageError == null) {
            response.data?.let { configureList(it) }
        } else {
            Log.e("FINANCIAL FINDALL ERROR", response.messageError)
            Snackbar.make(financial_layout, getString(R.string.nao_encontrou_lancamentos), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun configureList(releases: MutableList<FinancialRelease>) {
        viewModel.addAll(releases)
        configureEmptyView()

        with (financial_recyclerview) {
            adapter = FinancialReleaseAdapter(context!!, releases)
            financial_refresh.setOnRefreshListener(this@FinancialFragment)
        }
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            financial_empty_view.visibility = View.VISIBLE
            financial_recyclerview.visibility = View.GONE
        } else {
            financial_empty_view.visibility = View.GONE
            financial_recyclerview.visibility = View.VISIBLE
        }
    }

}
