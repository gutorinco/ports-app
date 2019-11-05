package br.com.suitesistemas.portsmobile.view.activity.search

import android.Manifest
import android.app.Activity
import android.content.Context
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
import br.com.suitesistemas.portsmobile.databinding.ActivityProductSearchBinding
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Product
import br.com.suitesistemas.portsmobile.model.enums.EConfigProductSearch
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.model.enums.ESystemType
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.PermissionActivity
import br.com.suitesistemas.portsmobile.view.activity.ScannerActivity
import br.com.suitesistemas.portsmobile.view.adapter.ProductAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.ProductSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_product_search.*

class ProductSearchActivity : SearchActivity(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<Product>?>> {

    companion object {
        private const val BARCODE = 1
        private const val REQUEST_PERMISSION = 2
    }
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
        val sharedPref = getSharedPreferences("config", Context.MODE_PRIVATE)
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(ProductSearchViewModel::class.java)
        with (viewModel) {
            initRepository(companyName)
            searchBy = EConfigProductSearch.valueOf(sharedPref.getString("productSearchBy", EConfigProductSearch.DESCRICAO.name)!!)
            isBarCode.value = searchBy == EConfigProductSearch.COD_BARRAS
        }
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
        with (viewModel) {
            searchTypeText.value = searchBy == EConfigProductSearch.DESCRICAO || searchBy == EConfigProductSearch.REFERENCIA
        }

        configureSearchBarHint()
        product_search_bar_home.setOnClickListener { onHomeNavigation() }
        product_search_bar_done.setOnClickListener(this)
        product_search_bar_query.setOnEditorActionListener(this)
        product_search_bar.requestFocus()
        product_search_bar_scanner.setOnClickListener {
            if (hasPermission(Manifest.permission.CAMERA)) {
                initScannerActivity()
            } else {
                with (Intent(this, PermissionActivity::class.java)) {
                    putExtra("icon", R.drawable.ic_camera_accent)
                    putExtra("name", "Câmera")
                    startActivityForResult(this, REQUEST_PERMISSION)
                }
            }
        }
    }

    private fun configureSearchBarHint() {
        with (viewModel) {
            val hint = when (searchBy) {
                EConfigProductSearch.CODIGO -> R.string.digite_codigo_produto
                EConfigProductSearch.COD_BARRAS -> R.string.digite_codigo_barras
                EConfigProductSearch.REFERENCIA -> R.string.digite_ref_produto
                EConfigProductSearch.DESCRICAO -> R.string.digite_nome_produto
            }
            product_search_bar_query.hint = getString(hint)
        }
    }

    private fun initScannerActivity() {
        val intent = Intent(this, ScannerActivity::class.java)
        startActivityForResult(intent, BARCODE)
    }

    private fun getNewAdapter(products: MutableList<Product>): ProductAdapter {
        val sharedPref = getSharedPreferences("config", Context.MODE_PRIVATE)
        val type = sharedPref!!.getString("systemType", ESystemType.A.name)
        return ProductAdapter(baseContext, products, ESystemType.valueOf(type), {
            delete(it)
        }, {
            onItemClicked(it, product_search)
        })
    }

    private fun configureList(products: MutableList<Product>) {
        productAdapter = getNewAdapter(products)
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
        if (data != null)
             setAdapter(viewModel.completeList)
        else setAdapter(data)
    }

    private fun setAdapter(data: List<Product>?) = data?.let { productAdapter.setAdapter(it) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BARCODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val barcode = it.getStringExtra("scanner_response")
                    product_search_bar_query.setText(barcode)
                    onClick(null)
                }
            }
        } else if (requestCode == REQUEST_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                requestPermission(Manifest.permission.CAMERA)
            } else {
                showMessage(product_search, R.string.permissao_nao_concedida)
            }
        }
        hideKeyboard()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val denied = grantResults.filter { it != PackageManager.PERMISSION_GRANTED }
        if (denied.isEmpty()) {
            initScannerActivity()
        } else {
            showMessage(product_search, R.string.permissao_nao_concedida)
        }
    }

    private fun delete(position: Int) {
        val firebaseToken = FirebaseUtils.getToken(this)
        with (viewModel) {
            searching.value = true
            fetchAllColorsBy(position)
            productColorResponse.observe(this@ProductSearchActivity, observerHandler({
                addRemovedProductColors(it)
                val product = getBy(position)
                delete(product.num_codigo_online, position, firebaseToken)
            }, {
                showMessage(product_search, getString(R.string.falha_remover_cores))
            }, {
                searching.value = false
            }))
        }
    }

    override fun deleteRollback() {
        with (viewModel) {
            searching.value = true
            deleteRollback("produto")
            rollbackResponse.observe(this@ProductSearchActivity, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
                removedObject = it
                this@ProductSearchActivity.productColorsDeleteRollback()
            }, {
                searching.value = false
                showMessage(product_search, it, getString(R.string.falha_desfazer_acao))
            }))
        }
    }

    private fun productColorsDeleteRollback() {
        with (viewModel) {
            productColorsDeleteRollback()
            productColorRollbackResponse.observe(this@ProductSearchActivity, observerHandler({}, {
                showMessage(product_search, getString(R.string.falha_desfazer_acao))
            }, {
                searching.value = false
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        val data = Intent()
        data.putExtra("product_response", viewModel.getBy(position))
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}