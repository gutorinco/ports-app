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
import br.com.suitesistemas.portsmobile.databinding.ActivitySaleSearchBinding
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Sale
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.SaleAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.SaleSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sale_search.*

class SaleSearchActivity : SearchActivity(),
    OnItemClickListener, Observer<ApiResponse<MutableList<Sale>?>> {

    lateinit var viewModel: SaleSearchViewModel
    private lateinit var saleAdapter: SaleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(sale_search)
        super.initDatePicker(sale_search_bar_query) { sale_search_bar_query.text = null }
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        val isGetMethod = intent.getBooleanExtra("get", false)
        val loggedUser = getLoggedUser()

        viewModel = ViewModelProviders.of(this).get(SaleSearchViewModel::class.java)
        with (viewModel) {
            initRepository(companyName, isGetMethod)
            onlyCurrentUser = loggedUser.permissoes.flg_visualizar_venda == EYesNo.S
            currentUser = loggedUser.pessoa
        }
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivitySaleSearchBinding>(this, R.layout.activity_sale_search)
            .apply {
                lifecycleOwner = this@SaleSearchActivity
                viewModel = this@SaleSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        sale_search_bar_home.setOnClickListener { onHomeNavigation() }
        sale_search_bar_done.setOnClickListener(this)
        sale_search_bar_query.setOnClickListener { dateDialog.show() }
        sale_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, sale_search) {
            hideKeyboard()
            val search = sale_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Sale>?>) {
        viewModel.searching.value = false
        if (response.messageError == null) {
            onResponse(response.data, response.operation)
        } else {
            showMessageError(sale_search, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun onResponse(data: List<Sale>?, operation: EHttpOperation) {
        when (operation) {
            EHttpOperation.GET -> data?.let { customers -> viewModel.addAll(customers as MutableList<Sale>) }
            EHttpOperation.DELETE -> deleted()
            EHttpOperation.ROLLBACK -> Snackbar.make(sale_search, getString(R.string.acao_desfeita), Snackbar.LENGTH_LONG).show()
            else -> {}
        }
        if (data != null)
             setAdapter(viewModel.completeList)
        else setAdapter(data)
        viewModel.searching.value = false
    }

    private fun setAdapter(data: List<Sale>?) = data?.let { saleAdapter.setAdapter(it) }

    private fun configureList(sales: MutableList<Sale>) {
        saleAdapter = SaleAdapter(baseContext, sales, {
            delete(it)
        }, {
            onItemClicked(it, sale_search)
        })
        with(sale_search_recycler_view) {
            adapter = saleAdapter
            addSwipe(configureSwipe())
            if (viewModel.isGetMethod)
                addOnItemClickListener(this@SaleSearchActivity)
        }
    }

    private fun configureSwipe(): SwipeToDeleteCallback {
        return SwipeToDeleteCallback(baseContext) { itemPosition ->
            executeAfterLoaded(viewModel.searching.value!!, sale_search) {
                delete(itemPosition)
            }
        }
    }

    private fun delete(position: Int) {
        val firebaseToken = FirebaseUtils.getToken(this)
        with (viewModel) {
            searching.value = true
            findAllItemsBySale(position)
            itemResponse.observe(this@SaleSearchActivity, Observer {
                deleteSale(position, it.data!!, firebaseToken)
            })
        }
    }

    override fun deleteRollback() {
        with (viewModel) {
            searching.value = true
            deleteRollback("venda")
            rollbackResponse.observe(this@SaleSearchActivity, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
                if (existItems())
                    this@SaleSearchActivity.deleteItemRollback(it)
            }, {
                showMessageError(sale_search, it, EHttpOperation.ROLLBACK)
                configureList(getList())
            }, {
                searching.value = false
            }))
        }
    }

    private fun deleteItemRollback(sale: Sale) {
        with (viewModel) {
            deleteItemRollback(sale)
            itemRollbackResponse.observe(this@SaleSearchActivity, observerHandler({
                onChanged(ApiResponse(getList(), EHttpOperation.ROLLBACK))
            }, {
                onChanged(ApiResponse(it, EHttpOperation.POST))
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        val data = Intent()
        data.putExtra("sale_response", viewModel.getBy(position))

        setResult(Activity.RESULT_OK, data)
        finish()
    }

}