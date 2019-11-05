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
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.databinding.ActivitySelectColorSearchBinding
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Color
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.SelectColorAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.ColorSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_select_color_search.*

class SelectColorSearchActivity : SearchActivity(),
    OnItemClickListener, Observer<ApiResponse<MutableList<Color>?>> {

    lateinit var viewModel: ColorSearchViewModel
    private lateinit var selectColorAdapter: SelectColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_color_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(select_color_search)
        configureList(viewModel.getList())
        configureSearchBar()
        configureButton()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)

        viewModel = ViewModelProviders.of(this).get(ColorSearchViewModel::class.java)
        viewModel.initRepository(companyName)
        viewModel.onSelected {
            with (select_color_search_button) {
                if (it) {
                    if (visibility == View.GONE)
                        showFromBottom()
                } else if (visibility == View.VISIBLE) {
                    hideToBottom()
                }
            }
        }
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivitySelectColorSearchBinding>(this, R.layout.activity_select_color_search)
            .apply {
                lifecycleOwner = this@SelectColorSearchActivity
                viewModel = this@SelectColorSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        select_color_search_bar_home.setOnClickListener { onBackPressed() }
        select_color_search_bar_done.setOnClickListener(this)
        select_color_search_bar_query.setOnEditorActionListener(this)
        select_color_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        val search = select_color_search_bar_query.text.toString()
        viewModel.search(search)
        viewModel.response.observe(this, this)
    }

    private fun configureButton() {
        select_color_search_button.setOnClickListener { sendSelectedColors() }
    }

    private fun sendSelectedColors() {
        val data = Intent()

        val bundle = Bundle()
        bundle.putParcelableArrayList("colors_selected", ArrayList(viewModel.getSelectedList()))
        data.putExtras(bundle)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onChanged(response: ApiResponse<MutableList<Color>?>) {
        if (response.messageError == null) {
            response.data?.let {
                viewModel.addAll(it)
                selectColorAdapter.setAdapter(viewModel.completeList)
            }
        } else {
            Log.e("PRODUCT SEARCH ERROR:", response.messageError)
            Snackbar.make(select_color_search, getString(R.string.nenhum_resultado), Snackbar.LENGTH_LONG).show()
        }

        viewModel.searching.value = false
    }

    private fun configureList(colors: MutableList<Color>) {
        selectColorAdapter = SelectColorAdapter(baseContext, colors) { checked, position ->
            val color = viewModel.getBy(position)
            viewModel.onChecked(checked, color)
        }
        with (select_color_search_recycler_view) {
            adapter = selectColorAdapter
            addOnItemClickListener(this@SelectColorSearchActivity)
            hideButtonOnScroll(select_color_search_button)
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        val color = viewModel.getBy(position)
        val linearLayout = view as LinearLayout
        val checkbox = linearLayout.getChildAt(0) as CheckBox
        checkbox.isChecked = !checkbox.isChecked
        viewModel.onChecked(checkbox.isChecked, color)
    }

    override fun deleteRollback() = TODO("not implemented")

}