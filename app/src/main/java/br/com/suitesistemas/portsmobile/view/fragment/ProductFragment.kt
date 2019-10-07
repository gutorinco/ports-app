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
import br.com.suitesistemas.portsmobile.custom.observer.observerHandler
import br.com.suitesistemas.portsmobile.custom.progress_bar.hide
import br.com.suitesistemas.portsmobile.custom.progress_bar.show
import br.com.suitesistemas.portsmobile.custom.recycler_view.*
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.setTitle
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.custom.view.showMessageError
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.ProductFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.ProductSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ProductAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.ProductViewModel
import kotlinx.android.synthetic.main.fragment_product.*

class ProductFragment : BasicFragment<Product, ProductAdapter>(),
            OnItemClickListener,
            SwipeRefreshLayout.OnRefreshListener,
            Observer<ApiResponse<MutableList<Product>?>> {

    lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        configureObserver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(product_layout, ProductAdapter(context!!, viewModel.getSortingList(), {
            delete(it)
        }, {
            edit(it)
        }))
        setTitle(R.string.produtos)
    }

    override fun onPause() {
        super.onPause()
        product_button.hideToBottom()
    }

    override fun onRefresh() {
        when (product_progressbar.isIndeterminate) {
            true -> product_refresh.isRefreshing = false
            false -> refresh()
        }
    }

    private fun refresh() {
        product_progressbar.show()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded(product_progressbar.isIndeterminate, product_layout) {
            val intent = Intent(activity, ProductSearchActivity::class.java)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        viewModel.fetchAllProducts(companyName)
        viewModel.response.observe(this, this)
    }

    override fun onChanged(response: ApiResponse<MutableList<Product>?>) {
        product_progressbar.hide()
        product_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded(product_progressbar.isIndeterminate, product_layout) {
                    configureButton()
                    response.data?.let { products ->
                        viewModel.addAll(products)
                        configureList(products)
                    }
                }
            }
        } else {
            showMessageError(product_layout, response.messageError!!, response.operation)
            configureList(viewModel.getSortingList())
        }
    }

    private fun configureButton() {
        with (product_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded(product_progressbar.isIndeterminate, product_layout) {
                    val intent = Intent(activity, ProductFormActivity::class.java)
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
                        val productResponse = it.getParcelableExtra("product_response") as Product
                        viewModel.add(productResponse)
                    }
                    UPDATE_REQUEST_CODE -> {
                        val productResponse = it.getParcelableExtra("product_response") as Product
                        viewModel.updateList(productResponse)
                    }
                    GET_REQUEST_CODE -> {
                        val productResponse = it.getParcelableExtra("product_response") as Product
                        editProductSelected(productResponse)
                    }
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
        product_button.showFromBottom()
    }

    private fun editProductSelected(product: Product) {
        val intent = Intent(activity, ProductFormActivity::class.java)
        intent.putExtra("product", product)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

    private fun configureList(products: MutableList<Product>) {
        product_refresh.setOnRefreshListener(this@ProductFragment)
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(products)

        with (product_recyclerview) {
            adapter = customAdapter
            addOnItemClickListener(this@ProductFragment)
            hideButtonOnScroll(product_button)
        }
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            product_empty_view.visibility = View.VISIBLE
            product_recyclerview.visibility = View.GONE
        } else {
            product_empty_view.visibility = View.GONE
            product_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun configureSwipe() {
        product_recyclerview.addSwipe(SwipeToDeleteCallback(context!!) { itemPosition ->
            executeAfterLoaded(product_progressbar.isIndeterminate, product_layout) {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        val firebaseToken = FirebaseUtils.getToken(context!!)
        product_progressbar.show()
        viewModel.fetchAllColorsBy(position)
        viewModel.productColorResponse.observe(this, observerHandler({
            viewModel.addRemovedProductColors(it)
            viewModel.delete(position, firebaseToken)
        }, {
            showMessage(product_layout, getString(R.string.falha_remover_cores))
        }, {
            product_progressbar.hide()
        }))
    }

    override fun deleteRollback() {
        product_progressbar.show()
        viewModel.deleteRollback()
        viewModel.rollbackResponse.observe(this, observerHandler({
            viewModel.add(it, EHttpOperation.ROLLBACK)
            viewModel.removedObject = it
            productColorsDeleteRollback()
        }, {
            product_progressbar.hide()
            showMessage(product_layout, it, getString(R.string.falha_desfazer_acao))
        }))
    }

    private fun productColorsDeleteRollback() {
        viewModel.productColorsDeleteRollback()
        viewModel.productColorRollbackResponse.observe(this, observerHandler({}, {
            showMessage(product_layout, getString(R.string.falha_desfazer_acao))
        }, {
            product_progressbar.hide()
        }))
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded(product_progressbar.isIndeterminate, product_layout) {
            edit(position)
        }
    }

    private fun edit(position: Int) {
        val product = viewModel.getBy(position)
        val intent = Intent(activity, ProductFormActivity::class.java)
        intent.putExtra("product", product)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

}