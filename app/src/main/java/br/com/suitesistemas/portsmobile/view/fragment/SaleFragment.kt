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
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.SaleFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.SaleSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.SaleAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.SaleViewModel
import kotlinx.android.synthetic.main.fragment_sale.*

class SaleFragment : BasicFragment<Sale, SaleAdapter>(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<Sale>?>> {

    private lateinit var viewModel: SaleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SaleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sale, container, false)
        setHasOptionsMenu(true)
        configureObserver()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(sale_layout, SaleAdapter(context!!, viewModel.getList(), {
            delete(it)
        }, {
            edit(viewModel.getBy(it))
        }))
        setTitle(R.string.vendas)
    }

    override fun getFloatingButton() = sale_button
    override fun getProgressBar() = sale_progressbar
    override fun getRefresh() = sale_refresh
    override fun getLayout() = sale_layout

    override fun refresh() {
        showProgress()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded {
            val intent = Intent(activity, SaleSearchActivity::class.java)
            intent.putExtra("get", true)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        with (viewModel) {
            initRepositories(companyName)
            fetchAll()
            response.observe(this@SaleFragment, this@SaleFragment)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Sale>?>) {
        hideProgress()
        sale_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
               executeAfterLoaded {
                    configureButton()
                    response.data?.let { sales ->
                        viewModel.addAll(sales)
                        configureList(sales)
                    }
                }
            }
        } else {
            showMessageError(sale_layout, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun configureButton() {
        with (sale_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded {
                    showProgress()
                    val intent = Intent(activity, SaleFormActivity::class.java)
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
                        val saleResponse = it.getParcelableExtra("sale_response") as Sale
                        viewModel.add(saleResponse)
                    }
                    UPDATE_REQUEST_CODE -> {
                        val saleResponse = it.getParcelableExtra("sale_response") as Sale
                        viewModel.updateList(saleResponse)
                    }
                    GET_REQUEST_CODE -> {
                        val saleResponse = it.getParcelableExtra("sale_response") as Sale
                        edit(saleResponse, UPDATE_REQUEST_CODE)
                    }
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
        sale_button.showFromBottom()
        hideProgress()
    }

    private fun configureList(sales: MutableList<Sale>) {
        sale_refresh.setOnRefreshListener(this@SaleFragment)
        configureSwipe()
        configureEmptyView()
        customAdapter.setAdapter(sales)

        with (sale_recyclerview) {
            adapter = customAdapter
            addOnItemClickListener(this@SaleFragment)
            hideButtonOnScroll(sale_button)
        }
    }

    private fun configureSwipe() {
        sale_recyclerview.addSwipe(SwipeToDeleteCallback(context!!) { itemPosition ->
            executeAfterLoaded {
                delete(itemPosition)
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
        val firebaseToken = FirebaseUtils.getToken(context!!)
        showProgress()
        viewModel.findAllItemsBySale(position)
        viewModel.itemResponse.observe(this, Observer { viewModel.deleteSale(position, it.data!!, firebaseToken) })
    }

    override fun deleteRollback() {
        showProgress()
        with (viewModel) {
            deleteRollback()
            rollbackResponse.observe(this@SaleFragment, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
                if (existItems()) {
                    this@SaleFragment.deleteItemRoolback(it)
                } else {
                    hideProgress()
                }
            }, {
                showMessageError(sale_layout, it, EHttpOperation.ROLLBACK)
                configureList(getList())
                hideProgress()
            }))
        }
    }

    private fun deleteItemRoolback(sale: Sale) {
        with (viewModel) {
            deleteItemRollback(sale)
            itemRollbackResponse.observe(this@SaleFragment, observerHandler({
                onChanged(ApiResponse(getList(), EHttpOperation.ROLLBACK))
            }, {
                onChanged(ApiResponse(it, EHttpOperation.POST))
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded {
            edit(viewModel.getBy(position), UPDATE_REQUEST_CODE)
        }
    }

    private fun edit(sale: Sale, requestCode: Int = GET_REQUEST_CODE) {
        showProgress()
        val intent = Intent(activity, SaleFormActivity::class.java)
        intent.putExtra("sale", sale)
        startActivityForResult(intent, requestCode)
    }

}
