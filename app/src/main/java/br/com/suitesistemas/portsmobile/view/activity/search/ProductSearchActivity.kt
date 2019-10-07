package br.com.suitesistemas.portsmobile.view.activity.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.observer.observerHandler
import br.com.suitesistemas.portsmobile.custom.recycler_view.OnItemClickListener
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.custom.recycler_view.addOnItemClickListener
import br.com.suitesistemas.portsmobile.custom.recycler_view.addSwipe
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.hideKeyboard
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.custom.view.showMessageError
import br.com.suitesistemas.portsmobile.databinding.ActivityProductSearchBinding
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.ProductAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.ProductSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_product_search.*

class ProductSearchActivity : SearchActivity(), OnItemClickListener, Observer<ApiResponse<MutableList<Product>?>> {

    lateinit var type: String
    lateinit var viewModel: ProductSearchViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(product_search)
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(ProductSearchViewModel::class.java)
        viewModel.initRepository(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityProductSearchBinding>(this, R.layout.activity_product_search)
            .apply {
                lifecycleOwner = this@ProductSearchActivity
                viewModel = this@ProductSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        product_search_bar_home.setOnClickListener { onHomeNavigation() }
        product_search_bar_done.setOnClickListener(this)
        product_search_bar_query.setOnEditorActionListener(this)
        product_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, product_search) {
            hideKeyboard()
            val search = product_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Product>?>) {
        viewModel.searching.value = false
        if (response.messageError == null) {
            onResponse(response.data, response.operation)
        } else {
            showMessageError(product_search, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun onResponse(data: List<Product>?, operation: EHttpOperation) {
        when (operation) {
            EHttpOperation.GET -> data?.let { products -> viewModel.addAll(products as MutableList<Product>) }
            EHttpOperation.DELETE -> deleted()
            EHttpOperation.ROLLBACK -> Snackbar.make(product_search, getString(R.string.acao_desfeita), Snackbar.LENGTH_LONG).show()
            else -> {}
        }
        setAdapter(data)
    }

    private fun setAdapter(data: List<Product>?) = data?.let { productAdapter.setAdapter(it) }

    private fun configureList(products: MutableList<Product>) {
        productAdapter = ProductAdapter(baseContext, products, {
            delete(it)
        }, {
            onItemClicked(it, product_search)
        })
        configureSwipe()
        with (product_search_recycler_view) {
            adapter = productAdapter
            addOnItemClickListener(this@ProductSearchActivity)
        }
    }

    private fun configureSwipe() {
        product_search_recycler_view.addSwipe(SwipeToDeleteCallback(baseContext) { itemPosition ->
            executeAfterLoaded(viewModel.searching.value!!, product_search) {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        val firebaseToken = FirebaseUtils.getToken(this)
        viewModel.searching.value = true
        viewModel.fetchAllColorsBy(position)
        viewModel.productColorResponse.observe(this, observerHandler({
            viewModel.addRemovedProductColors(it)
            val product = viewModel.getBy(position)
            viewModel.delete(product.num_codigo_online, position, firebaseToken)
        }, {
            showMessage(product_search, getString(R.string.falha_remover_cores))
        }, {
            viewModel.searching.value = false
        }))
    }

    override fun deleteRollback() {
        viewModel.searching.value = true
        viewModel.deleteRollback("produto")
        viewModel.rollbackResponse.observe(this, observerHandler({
            viewModel.add(it, EHttpOperation.ROLLBACK)
            viewModel.removedObject = it
            productColorsDeleteRollback()
        }, {
            viewModel.searching.value = false
            showMessage(product_search, it, getString(R.string.falha_desfazer_acao))
        }))
    }

    private fun productColorsDeleteRollback() {
        viewModel.productColorsDeleteRollback()
        viewModel.productColorRollbackResponse.observe(this, observerHandler({}, {
            showMessage(product_search, getString(R.string.falha_desfazer_acao))
        }, {
            viewModel.searching.value = false
        }))
    }

    override fun onItemClicked(position: Int, view: View) {
        val data = Intent()
        data.putExtra("product_response", viewModel.getBy(position))
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}