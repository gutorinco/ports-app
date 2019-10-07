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
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.ColorSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ColorAdapter
import br.com.suitesistemas.portsmobile.view.dialog.ColorFormDialog
import br.com.suitesistemas.portsmobile.viewModel.list.ColorViewModel
import kotlinx.android.synthetic.main.fragment_color.*

class ColorFragment : BasicFragment<Color, ColorAdapter>(),
        OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        Observer<ApiResponse<MutableList<Color>?>> {

    lateinit var viewModel: ColorViewModel
    private lateinit var dialog: ColorFormDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ColorViewModel::class.java)
        configureObserver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_color, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(color_layout, ColorAdapter(context!!, viewModel.getSortingList(), {
            delete(it)
        }, {
            showForm(viewModel.getBy(it))
        }))
        setTitle(R.string.cores)
    }

    override fun onPause() {
        super.onPause()
        color_button.hideToBottom()
    }

    override fun onRefresh() {
        when (color_progressbar.isIndeterminate) {
            true -> color_refresh.isRefreshing = false
            false -> refresh()
        }
    }

    private fun refresh() {
        color_progressbar.show()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded(color_progressbar.isIndeterminate, color_layout) {
            val intent = Intent(activity, ColorSearchActivity::class.java)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        viewModel.fetchAllColors(companyName)
        viewModel.response.observe(this, this)
    }

    override fun onChanged(response: ApiResponse<MutableList<Color>?>) {
        color_progressbar.hide()
        color_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded(color_progressbar.isIndeterminate, color_layout) {
                    configureButton()
                    response.data?.let { colors ->
                        viewModel.addAll(colors)
                        configureList(colors)
                    }
                }
            }
        } else {
            showMessageError(color_layout, response.messageError!!, response.operation)
            configureList(viewModel.getSortingList())
        }
    }

    private fun configureButton() {
        with (color_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded(color_progressbar.isIndeterminate, color_layout) {
                    showForm(Color())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                if (requestCode == GET_REQUEST_CODE) {
                    val colorResponse = it.getParcelableExtra("color_response") as Color
                    showForm(colorResponse)
                }
            }
        } else if (resultCode == Activity.BIND_IMPORTANT) { // Deleted on search
            refresh()
        }
        color_button.showFromBottom()
    }

    private fun configureList(colors: MutableList<Color>) {
        color_refresh.setOnRefreshListener(this@ColorFragment)
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(colors)

        with (color_recyclerview) {
            adapter = customAdapter
            addOnItemClickListener(this@ColorFragment)
            hideButtonOnScroll(color_button)
        }
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            color_empty_view.visibility = View.VISIBLE
            color_recyclerview.visibility = View.GONE
        } else {
            color_empty_view.visibility = View.GONE
            color_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun configureSwipe() {
        color_recyclerview.addSwipe(SwipeToDeleteCallback(context!!) { itemPosition ->
            executeAfterLoaded(color_progressbar.isIndeterminate, color_layout) {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        val firebaseToken = FirebaseUtils.getToken(context!!)
        color_progressbar.show()
        viewModel.delete(position, firebaseToken)
    }

    override fun deleteRollback() {
        color_progressbar.show()
        viewModel.deleteRollback()
        viewModel.rollbackResponse.observe(this, observerHandler({
            viewModel.add(it, EHttpOperation.ROLLBACK)
        }, {
            showMessage(color_layout, it, getString(R.string.falha_desfazer_acao))
        }, {
            color_progressbar.hide()
        }))
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded(color_progressbar.isIndeterminate, color_layout) {
            showForm(viewModel.getBy(position))
        }
    }

    private fun showForm(color: Color?) {
        viewModel.color = color
        dialog = ColorFormDialog.newInstance(color) {
            if (it.dsc_cor.isNullOrEmpty()) {
                dialog.dismiss()
                showMessage(color_layout, getString(R.string.insira_nome))
            } else {
                val firebaseToken = FirebaseUtils.getToken(context!!)
                viewModel.save(it, firebaseToken)
                if (color?.num_codigo_online.isNullOrEmpty())
                     configureInsertObserver()
                else configureUpdateObserver()
            }
        }
        dialog.show(fragmentManager!!)
    }

    private fun configureInsertObserver() {
        viewModel.insertResponse.observe(this, observerHandler({
            viewModel.add(it)
            dialog.dismiss()
        }, {
            showMessage(color_layout, it, getString(R.string.falha_inserir_cor))
        }))
    }

    private fun configureUpdateObserver() {
        viewModel.updateResponse.observe(this, observerHandler({
            viewModel.color?.version = it.version
            viewModel.updateList(viewModel.color!!)
            dialog.dismiss()
        }, {
            showMessage(color_layout, it, getString(R.string.falha_atualizar_cor))
        }))
    }

}
