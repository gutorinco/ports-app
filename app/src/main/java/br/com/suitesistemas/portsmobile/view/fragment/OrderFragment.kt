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
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Order
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.OrderFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.OrderSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.OrderAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.OrderViewModel
import kotlinx.android.synthetic.main.fragment_order.*

class OrderFragment : BasicFragment<Order, OrderAdapter>(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<Order>?>> {

    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        setHasOptionsMenu(true)
        configureObserver()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(order_layout, OrderAdapter(context!!, viewModel.getList(), {
            delete(it)
        }, {
            edit(viewModel.getBy(it))
        }))
        setTitle(R.string.pedidos)
    }

    override fun getFloatingButton() = order_button
    override fun getProgressBar() = order_progressbar
    override fun getRefresh() = order_refresh
    override fun getLayout() = order_layout

    override fun refresh() {
        showProgress()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded(order_progressbar.isIndeterminate, order_layout) {
            val intent = Intent(activity, OrderSearchActivity::class.java)
            intent.putExtra("get", true)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        with (viewModel) {
            initRepositories(companyName)
            fetchAll()
            response.observe(this@OrderFragment, this@OrderFragment)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Order>?>) {
        hideProgress()
        order_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded(order_progressbar.isIndeterminate, order_layout) {
                    configureButton()
                    response.data?.let { orders ->
                        viewModel.addAll(orders)
                        configureList(orders)
                    }
                }
            }
        } else {
            showMessageError(order_layout, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun configureButton() {
        with (order_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded(order_progressbar.isIndeterminate, order_layout) {
                    showProgress()
                    val intent = Intent(activity, OrderFormActivity::class.java)
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
                        val orderResponse = it.getParcelableExtra("order_response") as Order
                        viewModel.add(orderResponse)
                    }
                    UPDATE_REQUEST_CODE -> {
                        val orderResponse = it.getParcelableExtra("order_response") as Order
                        viewModel.updateList(orderResponse)
                    }
                    GET_REQUEST_CODE -> {
                        val orderResponse = it.getParcelableExtra("order_response") as Order
                        edit(orderResponse, UPDATE_REQUEST_CODE)
                    }
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
        order_button.showFromBottom()
        hideProgress()
    }

    private fun configureList(orders: MutableList<Order>) {
        order_refresh.setOnRefreshListener(this@OrderFragment)
        configureSwipe()
        configureEmptyView()
        customAdapter.setAdapter(orders)

        with (order_recyclerview) {
            adapter = customAdapter
            addOnItemClickListener(this@OrderFragment)
            hideButtonOnScroll(order_button)
        }
    }

    private fun configureSwipe() {
        order_recyclerview.addSwipe(SwipeToDeleteCallback(context!!) { itemPosition ->
            executeAfterLoaded(order_progressbar.isIndeterminate, order_layout) {
                delete(itemPosition)
            }
        })
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            order_empty_view.visibility = View.VISIBLE
            order_recyclerview.visibility = View.GONE
        } else {
            order_empty_view.visibility = View.GONE
            order_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun delete(position: Int) {
        showProgress()
        fetchItemsBy(position)
    }

    private fun fetchItemsBy(position: Int) {
        with (viewModel) {
            findAllItemsByOrder(position)
            itemResponse.observe(this@OrderFragment, observerHandler({
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
            gridResponse.observe(this@OrderFragment, observerHandler({
                val firebaseToken = FirebaseUtils.getToken(context!!)
                addRemovedGrids(it)
                deleteOrder(position, firebaseToken)
            }, {
                handleError(it, R.string.nao_encontrou_grade_modelo)
            }))
        }
    }

    override fun deleteRollback() {
        showProgress()
        with (viewModel) {
            deleteRollback()
            rollbackResponse.observe(this@OrderFragment, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
                if (existItems()) {
                    this@OrderFragment.deleteItemRollback(it)
                } else {
                    hideProgress()
                }
            }, {
                showMessageError(order_layout, it, EHttpOperation.ROLLBACK)
                configureList(getList())
                hideProgress()
            }))
        }
    }

    private fun deleteItemRollback(order: Order) {
        with (viewModel) {
           deleteItemRollback(order)
           itemRollbackResponse.observe(this@OrderFragment, observerHandler({
               if (existGrids()) {
                   this@OrderFragment.deleteGridRollback(order)
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
           gridRollbackResponse.observe(this@OrderFragment, observerHandler({}, {
               onChanged(ApiResponse(it, EHttpOperation.POST))
           }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded(order_progressbar.isIndeterminate, order_layout) {
            edit(viewModel.getBy(position), UPDATE_REQUEST_CODE)
        }
    }

    private fun edit(order: Order, requestCode: Int = GET_REQUEST_CODE) {
        showProgress()
        val intent = Intent(activity, OrderFormActivity::class.java)
        intent.putExtra("order", order)
        startActivityForResult(intent, requestCode)
    }

}
