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
import br.com.suitesistemas.portsmobile.databinding.ActivityOrderSearchBinding
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Order
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.OrderAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.OrderSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_order_search.*
import kotlinx.android.synthetic.main.fragment_order.*

class OrderSearchActivity : SearchActivity(),
    OnItemClickListener,
    Observer<ApiResponse<MutableList<Order>?>> {

    lateinit var viewModel: OrderSearchViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(order_search)
        super.initDatePicker(order_search_bar_query) { order_search_bar_query.text = null }
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        val isGetMethod = intent.getBooleanExtra("get", false)

        viewModel = ViewModelProviders.of(this).get(OrderSearchViewModel::class.java)
        viewModel.initRepositories(companyName, isGetMethod)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityOrderSearchBinding>(this, R.layout.activity_order_search)
            .apply {
                lifecycleOwner = this@OrderSearchActivity
                viewModel = this@OrderSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        order_search_bar_home.setOnClickListener { onHomeNavigation() }
        order_search_bar_done.setOnClickListener(this)
        order_search_bar_query.setOnClickListener { dateDialog.show() }
        order_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, order_search) {
            hideKeyboard()
            val search = order_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Order>?>) {
        viewModel.searching.value = false
        if (response.messageError == null) {
            onResponse(response.data, response.operation)
        } else {
            showMessageError(order_search, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun onResponse(data: List<Order>?, operation: EHttpOperation) {
        when (operation) {
            EHttpOperation.GET -> data?.let { customers -> viewModel.addAll(customers as MutableList<Order>) }
            EHttpOperation.DELETE -> deleted()
            EHttpOperation.ROLLBACK -> Snackbar.make(order_search, getString(R.string.acao_desfeita), Snackbar.LENGTH_LONG).show()
            else -> {}
        }
        if (data != null)
             setAdapter(viewModel.completeList)
        else setAdapter(data)
        viewModel.searching.value = false
    }

    private fun setAdapter(data: List<Order>?) = data?.let { orderAdapter.setAdapter(it) }

    private fun configureList(orders: MutableList<Order>) {
        orderAdapter = OrderAdapter(baseContext, orders, {
            delete(it)
        }, {
            onItemClicked(it, order_search)
        })
        with(order_search_recycler_view) {
            adapter = orderAdapter
            addSwipe(configureSwipe())
            if (viewModel.isGetMethod)
                addOnItemClickListener(this@OrderSearchActivity)
        }
    }

    private fun configureSwipe(): SwipeToDeleteCallback {
        return SwipeToDeleteCallback(baseContext) { itemPosition ->
            executeAfterLoaded(viewModel.searching.value!!, order_search) {
                delete(itemPosition)
            }
        }
    }

    private fun delete(position: Int) {
        viewModel.searching.value = true
        fetchItemsBy(position)
    }

    private fun fetchItemsBy(position: Int) {
        with (viewModel) {
            findAllItemsByOrder(position)
            itemResponse.observe(this@OrderSearchActivity, observerHandler({
                addRemovedItems(it)
                fetchGridsBy(position)
            }, {
                handleError(it, R.string.nao_encontrou_itens_pedido)
            }))
        }
    }

    private fun fetchGridsBy(position: Int) {
        with (viewModel) {
            findAllGridsByOrder(position)
            gridResponse.observe(this@OrderSearchActivity, observerHandler({
                val firebaseToken = FirebaseUtils.getToken(this@OrderSearchActivity)
                addRemovedGrids(it)
                deleteOrder(position, firebaseToken)
            }, {
                handleError(it, R.string.nao_encontrou_grade_modelo)
            }))
        }
    }

    override fun deleteRollback() {
        with (viewModel) {
            deleteRollback("pedido")
            rollbackResponse.observe(this@OrderSearchActivity, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
                if (existItems()) {
                    this@OrderSearchActivity.deleteItemRollback(it)
                } else {
                    searching.value = false
                }
            }, {
                showMessageError(order_layout, it, EHttpOperation.ROLLBACK)
                configureList(getList())
                searching.value = false
            }))
        }
    }

    private fun deleteItemRollback(order: Order) {
        with (viewModel) {
            deleteItemRollback(order)
            itemRollbackResponse.observe(this@OrderSearchActivity, observerHandler({
                if (existGrids()) {
                    this@OrderSearchActivity.deleteGridRollback(order)
                } else {
                    onChanged(ApiResponse(getList(), EHttpOperation.ROLLBACK))
                }
            }, {
                onChanged(ApiResponse(it, EHttpOperation.POST))
            }))
        }
    }

    private fun deleteGridRollback(order: Order) {
        with (viewModel) {
            deleteGridRollback(order)
            gridRollbackResponse.observe(this@OrderSearchActivity, observerHandler({}, {
                onChanged(ApiResponse(it, EHttpOperation.POST))
            }))
        }
    }

    private fun handleError(errorMessage: String, clientMessage: Int) {
        viewModel.searching.value = false
        showMessage(order_layout, errorMessage, getString(clientMessage))
    }

    override fun onItemClicked(position: Int, view: View) {
        val data = Intent()
        data.putExtra("order_response", viewModel.getBy(position))

        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
