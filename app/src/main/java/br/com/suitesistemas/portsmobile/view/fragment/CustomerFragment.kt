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
import br.com.suitesistemas.portsmobile.custom.progress_bar.hide
import br.com.suitesistemas.portsmobile.custom.progress_bar.show
import br.com.suitesistemas.portsmobile.custom.recycler_view.*
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.onChangedFailure
import br.com.suitesistemas.portsmobile.custom.view.setTitle
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.CustomerFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.PeopleSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.CustomerAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.CustomerViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_customer.*

class CustomerFragment : BasicFragment<Customer, CustomerAdapter>(),
            OnItemClickListener,
            SwipeRefreshLayout.OnRefreshListener,
            Observer<ApiResponse<MutableList<Customer>?>> {

    lateinit var viewModel: CustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CustomerViewModel::class.java)
        configureObserver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_customer, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(customer_layout, CustomerAdapter(context!!, activity!!, viewModel.getSortingList(), {
            startActivity(it)
        }, {
            showMessage(customer_layout, getString(it))
        }, {
            delete(it)
        }, {
            edit(it)
        }))
        setTitle(R.string.clientes)
    }

    override fun onPause() {
        super.onPause()
        customer_button.hideToBottom()
    }

    override fun onRefresh() {
        when (customer_progressbar.isIndeterminate) {
            true -> customer_refresh.isRefreshing = false
            false -> refresh()
        }
    }

    private fun refresh() {
        customer_progressbar.show()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded(customer_progressbar.isIndeterminate, customer_layout) {
            val intent = Intent(activity, PeopleSearchActivity::class.java)
            intent.putExtra("get", true)
            intent.putExtra("type", "C")
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        viewModel.fetchAllCustomers(companyName)
        viewModel.response.observe(this, this)
    }

    override fun onChanged(response: ApiResponse<MutableList<Customer>?>) {
        customer_progressbar.hide()
        customer_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded(customer_progressbar.isIndeterminate, customer_layout) {
                    configureButton()
                    response.data?.let { customers ->
                        viewModel.addAll(customers)
                        configureList(customers)
                    }
                }
            }
        } else {
            onChangedFailure(customer_layout, response.messageError!!, response.operation) {
                configureList(viewModel.getSortingList())
            }
        }
    }

    private fun configureButton() {
        with (customer_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded(customer_progressbar.isIndeterminate, customer_layout) {
                    val intent = Intent(activity, CustomerFormActivity::class.java)
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
                        val customerResponse = it.getSerializableExtra("customer_response") as Customer
                        viewModel.add(customerResponse)
                    }
                    UPDATE_REQUEST_CODE -> {
                        val customerResponse = it.getSerializableExtra("customer_response") as Customer
                        viewModel.updateList(customerResponse)
                    }
                    GET_REQUEST_CODE -> {
                        val customerResponse = it.getSerializableExtra("people_response") as Customer
                        editCustomerSelected(customerResponse)
                    }
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
        customer_button.showFromBottom()
    }

    private fun editCustomerSelected(customer: Customer) {
        val intent = Intent(activity, CustomerFormActivity::class.java)
        intent.putExtra("customer", customer)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

    private fun configureList(customers: MutableList<Customer>) {
        customer_refresh.setOnRefreshListener(this@CustomerFragment)
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(customers)

        with (customer_recyclerview) {
            adapter = customAdapter
            addOnItemClickListener(this@CustomerFragment)
            hideButtonOnScroll(customer_button)
        }
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            customer_empty_view.visibility = View.VISIBLE
            customer_recyclerview.visibility = View.GONE
        } else {
            customer_empty_view.visibility = View.GONE
            customer_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun configureSwipe() {
        customer_recyclerview.addSwipe(SwipeToDeleteCallback(context!!) { itemPosition ->
            executeAfterLoaded(customer_progressbar.isIndeterminate, customer_layout) {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        val firebaseToken = FirebaseUtils.getToken(context!!)
        customer_progressbar.show()
        viewModel.delete(position, firebaseToken)
    }

    override fun deleteRollback() {
        customer_progressbar.show()
        viewModel.deleteRollback()
        viewModel.rollbackResponse.observe(this, Observer {
            customer_progressbar.show()
            if (it.messageError == null) {
                it.data?.let { customer -> viewModel.add(customer, EHttpOperation.ROLLBACK) }
            } else {
                Snackbar.make(customer_layout, getString(R.string.falha_desfazer_acao), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded(customer_progressbar.isIndeterminate, customer_layout) {
            edit(position)
        }
    }

    private fun edit(position: Int) {
        val customer = viewModel.getBy(position)
        val intent = Intent(activity, CustomerFormActivity::class.java)
        intent.putExtra("customer", customer)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

}