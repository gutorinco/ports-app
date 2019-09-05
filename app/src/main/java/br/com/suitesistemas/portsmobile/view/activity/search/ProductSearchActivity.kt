package br.com.suitesistemas.portsmobile.view.activity.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.recycler_view.OnItemClickListener
import br.com.suitesistemas.portsmobile.custom.recycler_view.addOnItemClickListener
import br.com.suitesistemas.portsmobile.custom.view.hideKeyboard
import br.com.suitesistemas.portsmobile.databinding.ActivityProductSearchBinding
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.SelectProductAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.ProductSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_product_search.*

class ProductSearchActivity : SearchActivity(), OnItemClickListener, Observer<ApiResponse<MutableList<Product>?>> {

    lateinit var viewModel: ProductSearchViewModel
    private lateinit var selectProductAdapter: SelectProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(product_search)
        configureList(viewModel.getList())
        configureSearchBar()
        configureButton()
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
        product_search_bar_home.setOnClickListener { onBackPressed() }
        product_search_bar_done.setOnClickListener(this)
        product_search_bar_query.setOnEditorActionListener(this)
        product_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        val search = product_search_bar_query.text.toString()
        viewModel.search(search)
        viewModel.response.observe(this, this)
    }

    private fun configureButton() {
        product_search_button.setOnClickListener { sendSelectedProducts() }
    }

    private fun sendSelectedProducts() {
        val data = Intent()

        val bundle = Bundle()
        bundle.putParcelableArrayList("products_selected", ArrayList(viewModel.getSelectedList()))
        data.putExtras(bundle)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onChanged(response: ApiResponse<MutableList<Product>?>) {
        if (response.messageError == null) {
            response.data?.let {
                viewModel.addAll(it)
                selectProductAdapter.setAdapter(it)
            }
        } else {
            Log.e("PRODUCT SEARCH ERROR:", response.messageError)
            Snackbar.make(product_search, getString(R.string.nenhum_resultado), Snackbar.LENGTH_LONG).show()
        }

        viewModel.searching.value = false
    }

    private fun configureList(products: MutableList<Product>) {
        selectProductAdapter = SelectProductAdapter(baseContext, products) { checked, position ->
            viewModel.onChecked(checked, position)
        }
        with (product_search_recycler_view) {
            adapter = selectProductAdapter
            addOnItemClickListener(this@ProductSearchActivity)
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        val linearLayout = view as LinearLayout
        val checkbox = linearLayout.getChildAt(0) as CheckBox
        checkbox.isChecked = !checkbox.isChecked
        viewModel.onChecked(checkbox.isChecked, position)
    }

    override fun deleteRollback() = TODO("not implemented")

}
