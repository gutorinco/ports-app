package br.com.suitesistemas.portsmobile.view.fragment

import android.app.Activity
import android.content.Context
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
import br.com.suitesistemas.portsmobile.model.entity.Configuration
import br.com.suitesistemas.portsmobile.model.entity.Product
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.model.enums.ESystemType
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.form.ProductFormActivity
import br.com.suitesistemas.portsmobile.view.activity.search.ProductSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ProductAdapter
import br.com.suitesistemas.portsmobile.viewModel.list.ProductViewModel
import kotlinx.android.synthetic.main.fragment_product.*

class ProductFragment : BasicFragment<Product, ProductAdapter>(),
            OnItemClickListener,
            Observer<ApiResponse<MutableList<Product>?>> {

    lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onResume() {
        super.onResume()
        setTitle(R.string.produtos)
        viewModel.initRepositories(SharedPreferencesUtils.getCompanyName(activity!!))
        initSystemType()
    }

    private fun initSystemType() {
        showProgress()
        val sharedPref = activity!!.getSharedPreferences("config", Context.MODE_PRIVATE)
        val type = sharedPref!!.getString("systemType", null)
        if (type == null) {
            showProgress()
            with (viewModel) {
                fetchConfigurations()
                configResponse.observe(this@ProductFragment, observerHandler({
                    val config = if (it.isNotEmpty()) it[0] else Configuration()
                    systemType = config.flg_tipo_sistema
                    init()

                    with(sharedPref.edit()) {
                        putString("systemType", systemType.name)
                        apply()
                        commit()
                    }
                }, {
                    showMessage(getString(R.string.nao_encontrou_configuracoes))
                }, {
                    hideProgress()
                }))
            }
        } else {
            viewModel.systemType = ESystemType.valueOf(type)
            init()
        }
    }

    private fun init() {
        fetchAll()
        super.init(product_layout, ProductAdapter(context!!, viewModel.getList(), viewModel.systemType, {
            delete(it)
        }, {
            edit(viewModel.getBy(it), GET_REQUEST_CODE)
        }))
    }

    override fun getFloatingButton() = product_button
    override fun getProgressBar() = product_progressbar
    override fun getRefresh() = product_refresh
    override fun getLayout() = product_layout

    override fun refresh() {
        showProgress()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded {
            val intent = Intent(activity, ProductSearchActivity::class.java)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun fetchAll() {
        with (viewModel) {
            fetchAll()
            response.observe(this@ProductFragment, this@ProductFragment)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Product>?>) {
        hideProgress()
        product_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded {
                    configureButton()
                    response.data?.let { products ->
                        viewModel.addAll(products)
                        configureList(products)
                    }
                }
            }
        } else {
            showMessageError(product_layout, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun configureButton() {
        with (product_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded {
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
                        edit(productResponse, UPDATE_REQUEST_CODE)
                    }
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
        product_button.showFromBottom()
        hideProgress()
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
            executeAfterLoaded {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        showProgress()
        with (viewModel) {
            fetchAllColorsBy(position)
            productColorResponse.observe(this@ProductFragment, observerHandler({
                addRemovedProductColors(it)
                delete(position, getFirebaseToken())
            }, {
                showMessage(getString(R.string.falha_remover_cores))
            }, {
                hideProgress()
            }))
        }
    }

    override fun deleteRollback() {
        showProgress()
        with (viewModel) {
            deleteRollback()
            rollbackResponse.observe(this@ProductFragment, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
                removedObject = it
                this@ProductFragment.productColorsDeleteRollback()
            }, {
                handleError(it, R.string.falha_desfazer_acao)
            }))
        }
    }

    private fun productColorsDeleteRollback() {
        with (viewModel) {
            productColorsDeleteRollback()
            productColorRollbackResponse.observe(this@ProductFragment, observerHandler({}, {
                showMessage(getString(R.string.falha_desfazer_acao))
            }, {
                hideProgress()
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded {
            val product = viewModel.getBy(position)
            edit(product, UPDATE_REQUEST_CODE)
        }
    }

    private fun edit(product: Product, requestCode: Int) {
        showProgress()
        val intent = Intent(activity, ProductFormActivity::class.java)
        intent.putExtra("product", product)
        startActivityForResult(intent, requestCode)
    }

}