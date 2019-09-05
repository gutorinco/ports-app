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
import br.com.suitesistemas.portsmobile.custom.recycler_view.OnItemClickListener
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.custom.recycler_view.addOnItemClickListener
import br.com.suitesistemas.portsmobile.custom.recycler_view.addSwipe
import br.com.suitesistemas.portsmobile.custom.view.configure
import br.com.suitesistemas.portsmobile.custom.view.onChangedFailure
import br.com.suitesistemas.portsmobile.custom.view.setTitle
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.entity.SaleItem
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.SaleFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.SaleSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.SaleAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.SaleViewModel
import kotlinx.android.synthetic.main.fragment_sale.*

class SaleFragment : BasicFragment<Sale, SaleAdapter>(),
        OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        Observer<ApiResponse<MutableList<Sale>?>> {

    private lateinit var viewModel: SaleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SaleViewModel::class.java)
        configureObserver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sale, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(sale_layout, SaleAdapter(context!!, viewModel.getSortingList()))
        setTitle(R.string.vendas)
    }

    override fun initSearchActivity() {
        val intent = Intent(activity, SaleSearchActivity::class.java)
        intent.putExtra("get", true)
        startActivityForResult(intent, GET_REQUEST_CODE)
    }

    override fun onRefresh() {
        when (sale_progressbar.isIndeterminate) {
            true -> sale_refresh.isRefreshing = false
            false -> refresh()
        }
    }

    private fun refresh() {
        sale_progressbar.isIndeterminate = true
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        viewModel.fetchAllSales(companyName)
        viewModel.response.observe(this, this)
    }

    override fun onChanged(response: ApiResponse<MutableList<Sale>?>) {
        sale_progressbar.isIndeterminate = false
        sale_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                when (sale_progressbar.isIndeterminate) {
                    true -> showMessage(sale_layout, getString(R.string.aguarde_terminar))
                    false -> onButtonClicked(response)
                }
            }
        } else {
            onChangedFailure(sale_layout, response.messageError!!, response.operation)
            configureList(viewModel.getSortingList())
        }
    }

    private fun onButtonClicked(response: ApiResponse<MutableList<Sale>?>) {
        sale_button.configure {
            val intent = Intent(activity, SaleFormActivity::class.java)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        }
        response.data?.let { sales ->
            viewModel.addAll(sales)
            configureList(sales)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                when (requestCode) {
                    CREATE_REQUEST_CODE -> {
                        val saleResponse = it.getSerializableExtra("sale_response") as Sale
                        viewModel.add(saleResponse)
                    }
                    UPDATE_REQUEST_CODE -> {
                        val saleResponse = it.getSerializableExtra("sale_response") as Sale
                        viewModel.updateList(saleResponse)
                    }
                    GET_REQUEST_CODE -> {
                        val saleResponse = it.getSerializableExtra("sale_response") as Sale
                        editSaleSelected(saleResponse)
                    }
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
    }

    private fun configureList(sales: MutableList<Sale>) {
        configureSwipe()
        configureEmptyView()
        customAdapter.setAdapter(sales)

        with (sale_recyclerview) {
            adapter = customAdapter
            sale_refresh.setOnRefreshListener(this@SaleFragment)
            addOnItemClickListener(this@SaleFragment)
        }
    }

    private fun configureSwipe() {
        sale_recyclerview.addSwipe(SwipeToDeleteCallback(context!!) { itemPosition ->
            when (sale_progressbar.isIndeterminate) {
                true  -> showMessage(sale_layout, getString(R.string.aguarde_terminar))
                false -> delete(itemPosition)
            }
        })
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            sale_empty_view.visibility = View.VISIBLE
            sale_recyclerview.visibility = View.GONE
        } else {
            sale_empty_view.visibility = View.GONE
            sale_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun delete(position: Int) {
        sale_progressbar.isIndeterminate = true
        viewModel.findAllItemsBySale(position)
        viewModel.itemResponse.observe(this, Observer { viewModel.deleteSale(position, it.data!!) })
    }

    override fun deleteRollback() {
        sale_progressbar.isIndeterminate = true
        viewModel.deleteRollback()
        viewModel.rollbackResponse.observe(this, Observer {
            if (it.messageError == null) {
                it.data?.let { sale ->
                    if (viewModel.existItems()) {
                        viewModel.deleteItemRollback(sale)
                        viewModel.itemRollbackResponse.observe(this, getItemRollbackObserver())
                    } else {
                        viewModel.add(sale, EHttpOperation.ROLLBACK)
                    }
                }
            } else {
                onChangedFailure(sale_layout, it.messageError!!, EHttpOperation.ROLLBACK)
            }
        })
    }

    private fun getItemRollbackObserver(): Observer<ApiResponse<MutableList<SaleItem>?>> {
        return Observer {
            if (it.messageError == null) {
                onChanged(ApiResponse(viewModel.getSortingList(), EHttpOperation.ROLLBACK))
            } else {
                onChanged(ApiResponse(it.messageError!!, EHttpOperation.POST))
            }
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        when (sale_progressbar.isIndeterminate) {
            true -> showMessage(sale_layout, getString(R.string.aguarde_terminar))
            false -> edit(position)
        }
    }

    private fun edit(position: Int) {
        val sale = viewModel.getBy(position)
        val intent = Intent(activity, SaleFormActivity::class.java)
        intent.putExtra("sale", sale)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

    private fun editSaleSelected(sale: Sale) {
        val intent = Intent(activity, SaleFormActivity::class.java)
        intent.putExtra("sale", sale)
        startActivityForResult(intent, GET_REQUEST_CODE)
    }

}
