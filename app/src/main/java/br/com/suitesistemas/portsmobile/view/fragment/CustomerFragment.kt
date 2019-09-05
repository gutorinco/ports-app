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
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
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
        super.init(customer_layout, CustomerAdapter(context!!, viewModel.getSortingList()))
        setTitle(R.string.clientes)
    }

    override fun initSearchActivity() {
        val intent = Intent(activity, PeopleSearchActivity::class.java)
        intent.putExtra("get", true)
        intent.putExtra("type", "C")
        startActivityForResult(intent, GET_REQUEST_CODE)
    }

    override fun onRefresh() {
        when (customer_progressbar.isIndeterminate) {
            true -> customer_refresh.isRefreshing = false
            false -> refresh()
        }
    }

    private fun refresh() {
        customer_progressbar.isIndeterminate = true
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        viewModel.fetchAllCustomers(companyName)
        viewModel.response.observe(this, this)
    }

    override fun onChanged(response: ApiResponse<MutableList<Customer>?>) {
        customer_progressbar.isIndeterminate = false
        customer_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                when (customer_progressbar.isIndeterminate) {
                    true -> showMessage(customer_layout, getString(R.string.aguarde_terminar))
                    false -> onButtonClicked(response)
                }
            }
        } else {
            onChangedFailure(customer_layout, response.messageError!!, response.operation)
        }
    }

    private fun onButtonClicked(response: ApiResponse<MutableList<Customer>?>) {
        customer_button.configure {
            val intent = Intent(activity, CustomerFormActivity::class.java)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        }
        response.data?.let { customers ->
            viewModel.addAll(customers)
            configureList(customers)
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
    }

    private fun editCustomerSelected(customer: Customer) {
        val intent = Intent(activity, CustomerFormActivity::class.java)
        intent.putExtra("customer", customer)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

    private fun configureList(customers: MutableList<Customer>) {
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(customers)

        with (customer_recyclerview) {
            adapter = customAdapter
            addOnItemClickListener(this@CustomerFragment)
            customer_refresh.setOnRefreshListener(this@CustomerFragment)
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
            when (customer_progressbar.isIndeterminate) {
                true  -> Snackbar.make(customer_layout, getString(R.string.aguarde_terminar), Snackbar.LENGTH_LONG).show()
                false -> delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        customer_progressbar.isIndeterminate = true
        viewModel.delete(position)
    }

    override fun deleteRollback() {
        customer_progressbar.isIndeterminate = true
        viewModel.deleteRollback()
        viewModel.rollbackResponse.observe(this, Observer {
            customer_progressbar.isIndeterminate = true
            if (it.messageError == null) {
                it.data?.let { customer -> viewModel.add(customer, EHttpOperation.ROLLBACK) }
            } else {
                Snackbar.make(customer_layout, getString(R.string.falha_desfazer_acao), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    override fun onItemClicked(position: Int, view: View) {
        when (customer_progressbar.isIndeterminate) {
            true -> Snackbar.make(customer_layout, getString(R.string.aguarde_terminar), Snackbar.LENGTH_LONG).show()
            false -> edit(position)
        }
    }

    private fun edit(position: Int) {
        val customer = viewModel.getBy(position)
        val intent = Intent(activity, CustomerFormActivity::class.java)
        intent.putExtra("customer", customer)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

}