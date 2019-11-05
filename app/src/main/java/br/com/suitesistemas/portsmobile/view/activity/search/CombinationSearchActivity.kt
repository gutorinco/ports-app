package br.com.suitesistemas.portsmobile.view.activity.search

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.OnItemClickListener
import br.com.suitesistemas.portsmobile.custom.extensions.addOnItemClickListener
import br.com.suitesistemas.portsmobile.custom.extensions.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.extensions.hideKeyboard
import br.com.suitesistemas.portsmobile.databinding.ActivityCombinationSearchBinding
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.Combination
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.CombinationAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.CombinationSearchViewModel
import kotlinx.android.synthetic.main.activity_combination_search.*

class CombinationSearchActivity : SearchActivity(),
    OnItemClickListener, Observer<ApiResponse<MutableList<Combination>?>> {

    lateinit var viewModel: CombinationSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combination_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(combination_search)
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(CombinationSearchViewModel::class.java)
        viewModel.initRepository(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityCombinationSearchBinding>(this, R.layout.activity_combination_search)
            .apply {
                lifecycleOwner = this@CombinationSearchActivity
                viewModel = this@CombinationSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        combination_search_bar_home.setOnClickListener { onHomeNavigation() }
        combination_search_bar_done.setOnClickListener(this)
        combination_search_bar_query.setOnEditorActionListener(this)
        combination_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, combination_search) {
            hideKeyboard()
            val search = combination_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    private fun configureList(combinations: MutableList<Combination>) {
        with (combination_search_recycler_view) {
            adapter = CombinationAdapter(baseContext, combinations)
            addOnItemClickListener(this@CombinationSearchActivity)
        }
    }

    override fun deleteRollback() = throw RuntimeException("Not implemented!")

    override fun onItemClicked(position: Int, view: View) = throw RuntimeException("Not implemented!")

    override fun onChanged(t: ApiResponse<MutableList<Combination>?>?) = throw RuntimeException("Not implemented!")

}
