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
import br.com.suitesistemas.portsmobile.databinding.ActivitySelectCombinationSearchBinding
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.SelectCombinationAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.CombinationSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_select_combination_search.*

class SelectCombinationSearchActivity : SearchActivity(),
    OnItemClickListener, Observer<ApiResponse<MutableList<Combination>?>> {

    lateinit var viewModel: CombinationSearchViewModel
    private lateinit var selectCombinationAdapter: SelectCombinationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_combination_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(select_combination_search)
        configureList(viewModel.getList())
        configureSearchBar()
        configureButton()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)

        viewModel = ViewModelProviders.of(this).get(CombinationSearchViewModel::class.java)
        viewModel.initRepository(companyName)
        viewModel.onSelected {
            with (select_combination_search_button) {
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
            .setContentView<ActivitySelectCombinationSearchBinding>(this, R.layout.activity_select_combination_search)
            .apply {
                lifecycleOwner = this@SelectCombinationSearchActivity
                viewModel = this@SelectCombinationSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        select_combination_search_bar_home.setOnClickListener { onBackPressed() }
        select_combination_search_bar_done.setOnClickListener(this)
        select_combination_search_bar_query.setOnEditorActionListener(this)
        select_combination_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        val search = select_combination_search_bar_query.text.toString()
        viewModel.search(search)
        viewModel.response.observe(this, this)
    }

    private fun configureButton() {
        select_combination_search_button.setOnClickListener { sendSelectedCombinations() }
    }

    private fun sendSelectedCombinations() {
        val data = Intent()

        val bundle = Bundle()
        bundle.putParcelableArrayList("combinations_selected", ArrayList(viewModel.getSelectedList()))
        data.putExtras(bundle)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onChanged(response: ApiResponse<MutableList<Combination>?>) {
        if (response.messageError == null) {
            response.data?.let {
                viewModel.addAll(it)
                selectCombinationAdapter.setAdapter(viewModel.completeList)
            }
        } else {
            Log.e("COMBINAT SEARCH ERROR:", response.messageError)
            Snackbar.make(select_combination_search, getString(R.string.nenhum_resultado), Snackbar.LENGTH_LONG).show()
        }

        viewModel.searching.value = false
    }

    private fun configureList(combinations: MutableList<Combination>) {
        selectCombinationAdapter = SelectCombinationAdapter(baseContext, combinations) { checked, position ->
            val combination = viewModel.getBy(position)
            viewModel.onChecked(checked, combination)
        }
        with (select_combination_search_recycler_view) {
            adapter = selectCombinationAdapter
            addOnItemClickListener(this@SelectCombinationSearchActivity)
            hideButtonOnScroll(select_combination_search_button)
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        val combination = viewModel.getBy(position)
        val linearLayout = view as LinearLayout
        val checkbox = linearLayout.getChildAt(0) as CheckBox
        checkbox.isChecked = !checkbox.isChecked
        viewModel.onChecked(checkbox.isChecked, combination)
    }

    override fun deleteRollback() = TODO("not implemented")

}
