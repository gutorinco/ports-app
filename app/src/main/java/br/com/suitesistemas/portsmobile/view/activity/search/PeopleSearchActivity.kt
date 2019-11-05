package br.com.suitesistemas.portsmobile.view.activity.search

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.databinding.ActivityPeopleSearchBinding
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Customer
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.PermissionActivity
import br.com.suitesistemas.portsmobile.view.adapter.CustomerAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.PeopleSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_people_search.*

class PeopleSearchActivity : SearchActivity(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<Customer>?>> {

    companion object {
        private const val REQUEST_PERMISSION = 1
    }
    lateinit var type: String
    lateinit var viewModel: PeopleSearchViewModel
    private lateinit var customerAdapter: CustomerAdapter
    private var requestToStartActivity: String = ""
    private var paramToStartActivity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_people_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(people_search)
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        type = intent.getStringExtra("type")

        viewModel = ViewModelProviders.of(this).get(PeopleSearchViewModel::class.java)
        viewModel.initRepository(companyName, type)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityPeopleSearchBinding>(this, R.layout.activity_people_search)
            .apply {
                lifecycleOwner = this@PeopleSearchActivity
                viewModel = this@PeopleSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        people_search_bar_home.setOnClickListener { onHomeNavigation() }
        people_search_bar_done.setOnClickListener(this)
        people_search_bar_query.setOnEditorActionListener(this)
        people_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, people_search) {
            hideKeyboard()
            val search = people_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Customer>?>) {
        viewModel.searching.value = false
        if (response.messageError == null) {
            onResponse(response.data, response.operation)
        } else {
            showMessageError(people_search, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun onResponse(data: List<Customer>?, operation: EHttpOperation) {
        when (operation) {
            EHttpOperation.GET -> data?.let { customers -> viewModel.addAll(customers as MutableList<Customer>) }
            EHttpOperation.DELETE -> deleted()
            EHttpOperation.ROLLBACK -> Snackbar.make(people_search, getString(R.string.acao_desfeita), Snackbar.LENGTH_LONG).show()
            else -> {}
        }
        if (data != null)
             setAdapter(viewModel.completeList)
        else setAdapter(data)
    }

    private fun setAdapter(data: List<Customer>?) = data?.let { customerAdapter.setAdapter(it) }

    private fun configureList(customers: MutableList<Customer>) {
        customerAdapter = CustomerAdapter(baseContext, this, customers, {
            onDelete(it)
        }, {
            onEdit(it)
        }, { req, param ->
            requestToStartActivity = req
            paramToStartActivity = param
            handlePermissionsOnStartActivity()
        }, {
            showMessage(people_search, getString(it))
        })
        if (type == "C")
            configureSwipe()
        with (people_search_recycler_view) {
            adapter = customerAdapter
            addOnItemClickListener(this@PeopleSearchActivity)
        }
    }

    private fun handlePermissionsOnStartActivity() {
        if (requestToStartActivity == "call") {
            if (hasPermission(Manifest.permission.CALL_PHONE)) {
                makeCall(paramToStartActivity, people_search)
            } else {
                with(Intent(this, PermissionActivity::class.java)) {
                    putExtra("icon", R.drawable.ic_phone_accent)
                    putExtra("name", "Chamada")
                    startActivityForResult(this, REQUEST_PERMISSION)
                }
            }
        } else if (requestToStartActivity == "email") {
            sendEmail(paramToStartActivity, people_search)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                requestPermission(Manifest.permission.CALL_PHONE)
            } else {
                showMessage(people_search, R.string.permissao_nao_concedida)
            }
        }
        hideKeyboard()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val denied = grantResults.filter { it != PackageManager.PERMISSION_GRANTED }
        if (denied.isEmpty()) {
            handlePermissionsOnStartActivity()
        } else {
            showMessage(people_search, R.string.permissao_nao_concedida)
        }
    }

    private fun onDelete(position: Int) {
        if (type == "C")
            delete(position)
        else showMessage(people_search, getString(R.string.acao_exclusao_clientes))
    }

    private fun onEdit(position: Int) {
        if (type == "C")
            onItemClicked(position, people_search)
        else showMessage(people_search, getString(R.string.acao_exclusao_clientes))
    }

    private fun configureSwipe() {
        people_search_recycler_view.addSwipe(SwipeToDeleteCallback(baseContext) { itemPosition ->
            executeAfterLoaded(viewModel.searching.value!!, people_search) {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        val customer = viewModel.getBy(position)
        val firebaseToken = FirebaseUtils.getToken(this)
        viewModel.searching.value = true
        viewModel.delete(customer.num_codigo_online, position, firebaseToken)
    }

    override fun deleteRollback() {
        viewModel.searching.value = true
        viewModel.deleteRollback("pessoa")
        viewModel.rollbackResponse.observe(this,
            observerHandler({
                viewModel.add(it, EHttpOperation.ROLLBACK)
            }, {
                Snackbar.make(people_search, getString(R.string.falha_desfazer_acao), Snackbar.LENGTH_LONG).show()
                configureList(viewModel.getList())
            }, {
                viewModel.searching.value = false
            })
        )
    }

    override fun onItemClicked(position: Int, view: View) {
        val data = Intent()
        data.putExtra("people_response", viewModel.getBy(position))
        data.putExtra("type", type)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
