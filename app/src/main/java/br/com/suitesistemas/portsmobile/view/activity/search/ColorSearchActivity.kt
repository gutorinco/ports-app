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
import br.com.suitesistemas.portsmobile.databinding.ActivityColorSearchBinding
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.ColorAdapter
import br.com.suitesistemas.portsmobile.viewModel.search.ColorSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_color_search.*

class ColorSearchActivity : SearchActivity(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<Color>?>> {

    lateinit var viewModel: ColorSearchViewModel
    private lateinit var colorAdapter: ColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_search)

        super.hideActionBar()
        initViewModel()
        configureDataBinding()
        super.init(color_search)
        configureList(viewModel.getList())
        configureSearchBar()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(ColorSearchViewModel::class.java)
        viewModel.initRepository(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityColorSearchBinding>(this, R.layout.activity_color_search)
            .apply {
                lifecycleOwner = this@ColorSearchActivity
                viewModel = this@ColorSearchActivity.viewModel
            }
    }

    private fun configureSearchBar() {
        color_search_bar_home.setOnClickListener { onHomeNavigation() }
        color_search_bar_done.setOnClickListener(this)
        color_search_bar_query.setOnEditorActionListener(this)
        color_search_bar.requestFocus()
    }

    override fun onClick(v: View?) {
        executeAfterLoaded(viewModel.searching.value!!, color_search) {
            hideKeyboard()
            val search = color_search_bar_query.text.toString()
            viewModel.search(search)
            viewModel.response.observe(this, this)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Color>?>) {
        viewModel.searching.value = false
        if (response.messageError == null) {
            onResponse(response.data, response.operation)
        } else {
            showMessageError(color_search, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun onResponse(data: List<Color>?, operation: EHttpOperation) {
        when (operation) {
            EHttpOperation.GET -> data?.let { colors -> viewModel.addAll(colors as MutableList<Color>) }
            EHttpOperation.DELETE -> deleted()
            EHttpOperation.ROLLBACK -> Snackbar.make(color_search, getString(R.string.acao_desfeita), Snackbar.LENGTH_LONG).show()
            else -> {}
        }
        if (data != null)
             setAdapter(viewModel.completeList)
        else setAdapter(data)
    }

    private fun setAdapter(data: List<Color>?) = data?.let { colorAdapter.setAdapter(it) }

    private fun configureList(colors: MutableList<Color>) {
        colorAdapter = ColorAdapter(baseContext, colors, {
            delete(it)
        }, {
            onItemClicked(it, color_search)
        })
        configureSwipe()
        with (color_search_recycler_view) {
            adapter = colorAdapter
            addOnItemClickListener(this@ColorSearchActivity)
        }
    }

    private fun configureSwipe() {
        color_search_recycler_view.addSwipe(SwipeToDeleteCallback(baseContext) { itemPosition ->
            executeAfterLoaded(viewModel.searching.value!!, color_search) {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        val color = viewModel.getBy(position)
        val firebaseToken = FirebaseUtils.getToken(this)
        viewModel.searching.value = true
        viewModel.delete(color.num_codigo_online, position, firebaseToken)
    }

    override fun deleteRollback() {
        viewModel.searching.value = true
        viewModel.deleteRollback("cor")
        viewModel.rollbackResponse.observe(this,
            observerHandler({
                viewModel.add(it, EHttpOperation.ROLLBACK)
            }, {
                Snackbar.make(
                    color_search,
                    getString(R.string.falha_desfazer_acao),
                    Snackbar.LENGTH_LONG
                ).show()
                configureList(viewModel.getList())
            }, {
                viewModel.searching.value = false
            })
        )
    }

    override fun onItemClicked(position: Int, view: View) {
        val color = viewModel.getBy(position)
        val data = Intent()
        data.putExtra("color_response", color)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}
