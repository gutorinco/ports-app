package br.com.suitesistemas.portsmobile.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import br.com.suitesistemas.portsmobile.model.entity.Customer
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.PermissionActivity
import br.com.suitesistemas.portsmobile.view.activity.form.CustomerFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.PeopleSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.CustomerAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.CustomerViewModel
import kotlinx.android.synthetic.main.fragment_customer.*

class CustomerFragment : BasicFragment<Customer, CustomerAdapter>(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<Customer>?>> {

    companion object {
        private const val REQUEST_PERMISSION = 4
    }
    private var requestToStartActivity: String = ""
    private var paramToStartActivity: String = ""
    lateinit var viewModel: CustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CustomerViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_customer, container, false)
        setHasOptionsMenu(true)
        configureObserver()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(customer_layout, CustomerAdapter(context!!, activity!!, viewModel.getList(), {
            delete(it)
        }, {
            edit(viewModel.getBy(it), UPDATE_REQUEST_CODE)
        }, { req, param ->
            requestToStartActivity = req
            paramToStartActivity = param
            handlePermissionsOnStartActivity()
        }, {
            showMessage(getString(it))
        }))
        setTitle(R.string.clientes)
    }

    private fun handlePermissionsOnStartActivity() {
        if (requestToStartActivity == "call") {
            if (hasPermission(Manifest.permission.CALL_PHONE)) {
                makeCall(paramToStartActivity, customer_layout)
            } else {
                with(Intent(context, PermissionActivity::class.java)) {
                    putExtra("icon", R.drawable.ic_phone_accent)
                    putExtra("name", "Chamada")
                    startActivityForResult(this, REQUEST_PERMISSION)
                }
            }
        } else if (requestToStartActivity == "email") {
            sendEmail(paramToStartActivity, customer_layout)
        }
    }

    override fun getFloatingButton() = customer_button
    override fun getProgressBar() = customer_progressbar
    override fun getRefresh() = customer_refresh
    override fun getLayout() = customer_layout

    override fun refresh() {
        showProgress()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded {
            val intent = Intent(activity, PeopleSearchActivity::class.java)
            intent.putExtra("get", true)
            intent.putExtra("type", "C")
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        with(viewModel) {
            initRepositories(companyName)
            fetchAll()
            response.observe(this@CustomerFragment, this@CustomerFragment)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Customer>?>) {
        hideProgress()
        customer_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded {
                    configureButton()
                    response.data?.let { customers ->
                        viewModel.addAll(customers)
                        configureList(customers)
                    }
                }
            }
        } else {
            showMessageError(customer_layout, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun configureButton() {
        with(customer_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded {
                    val intent = Intent(activity, CustomerFormActivity::class.java)
                    startActivityForResult(intent, CREATE_REQUEST_CODE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CREATE_REQUEST_CODE -> {
                    data?.let {
                        val customerResponse = it.getParcelableExtra("customer_response") as Customer
                        viewModel.add(customerResponse)
                    }
                }
                UPDATE_REQUEST_CODE -> {
                    data?.let {
                        val customerResponse = it.getParcelableExtra("customer_response") as Customer
                        viewModel.updateList(customerResponse)
                    }
                }
                GET_REQUEST_CODE -> {
                    data?.let {
                        val customerResponse = it.getParcelableExtra("people_response") as Customer
                        edit(customerResponse, UPDATE_REQUEST_CODE)
                    }
                }
                REQUEST_PERMISSION -> {
                    requestPermission(Manifest.permission.CALL_PHONE)
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) {
            refresh()  // Delete on search
        } else if (requestCode == REQUEST_PERMISSION)  {
            if (resultCode == Activity.RESULT_OK) {
                requestPermission(Manifest.permission.CALL_PHONE)
            } else {
                showMessage(customer_layout, R.string.permissao_nao_concedida)
            }
        }

        customer_button.showFromBottom()
        hideProgress()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val denied = grantResults.filter { it != PackageManager.PERMISSION_GRANTED }
        if (denied.isEmpty()) {
            handlePermissionsOnStartActivity()
        } else {
            showMessage(customer_layout, R.string.permissao_nao_concedida)
        }
    }

    private fun configureList(customers: MutableList<Customer>) {
        customer_refresh.setOnRefreshListener(this@CustomerFragment)
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(customers)

        with(customer_recyclerview) {
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
            rollbackResponse.observe(this@CustomerFragment, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
            }, {
                showMessage(it, R.string.falha_desfazer_acao)
            }, {
                hideProgress()
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded {
            edit(viewModel.getBy(position), UPDATE_REQUEST_CODE)
        }
    }

    private fun edit(customer: Customer, requestCode: Int) {
        showProgress()
        val intent = Intent(activity, CustomerFormActivity::class.java)
        intent.putExtra("customer", customer)
        startActivityForResult(intent, requestCode)
    }

}