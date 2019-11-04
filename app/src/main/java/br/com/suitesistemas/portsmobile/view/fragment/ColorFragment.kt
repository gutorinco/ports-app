package br.com.suitesistemas.portsmobile.view.fragment

import android.app.Activity
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
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.ColorSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ColorAdapter
import br.com.suitesistemas.portsmobile.view.dialog.ColorFormDialog
import br.com.suitesistemas.portsmobile.viewModel.list.ColorViewModel
import kotlinx.android.synthetic.main.fragment_color.*

class ColorFragment : BasicFragment<Color, ColorAdapter>(),
        OnItemClickListener,
        Observer<ApiResponse<MutableList<Color>?>> {

    lateinit var viewModel: ColorViewModel
    private lateinit var dialog: ColorFormDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ColorViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_color, container, false)
        setHasOptionsMenu(true)
        configureObserver()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.init(color_layout, ColorAdapter(context!!, viewModel.getList(), {
            delete(it)
        }, {
            showForm(viewModel.getBy(it))
        }))
        setTitle(R.string.cores)
    }

    override fun getFloatingButton() = color_button
    override fun getProgressBar() = color_progressbar
    override fun getRefresh() = color_refresh
    override fun getLayout() = color_layout

    override fun refresh() {
        showProgress()
        viewModel.refresh()
        viewModel.refreshResponse.observe(this, this)
    }

    override fun initSearchActivity() {
        executeAfterLoaded {
            val intent = Intent(activity, ColorSearchActivity::class.java)
            startActivityForResult(intent, GET_REQUEST_CODE)
        }
    }

    private fun configureObserver() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        with(viewModel) {
            initRepositories(companyName)
            fetchAll()
            response.observe(this@ColorFragment, this@ColorFragment)
        }
    }

    override fun onChanged(response: ApiResponse<MutableList<Color>?>) {
        hideProgress()
        color_refresh.isRefreshing = false

        if (response.messageError == null) {
            onChangedResponse(response.data, response.operation) {
                executeAfterLoaded {
                    configureButton()
                    response.data?.let { colors ->
                        viewModel.addAll(colors)
                        configureList(colors)
                    }
                }
            }
        } else {
            showMessageError(color_layout, response.messageError!!, response.operation)
            configureList(viewModel.getList())
        }
    }

    private fun configureButton() {
        with(color_button) {
            showFromBottom()
            setOnClickListener {
                executeAfterLoaded {
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
        hideProgress()
    }

    private fun configureList(colors: MutableList<Color>) {
        color_refresh.setOnRefreshListener(this@ColorFragment)
        configureEmptyView()
        configureSwipe()
        customAdapter.setAdapter(colors)

        with(color_recyclerview) {
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
            executeAfterLoaded {
                delete(itemPosition)
            }
        })
    }

    private fun delete(position: Int) {
        showProgress()
        viewModel.delete(position, getFirebaseToken())
    }

    override fun deleteRollback() {
        showProgress()
        with (viewModel) {
            deleteRollback()
            rollbackResponse.observe(this@ColorFragment, observerHandler({
                add(it, EHttpOperation.ROLLBACK)
            }, {
                showMessage(it, R.string.falha_desfazer_acao)
            }, {
                hideProgress()
            }))
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        executeAfterLoaded {
            showForm(viewModel.getBy(position))
        }
    }

    private fun showForm(color: Color?) {
        viewModel.color = color
        dialog = ColorFormDialog.newInstance(color) {
            if (it.dsc_cor.isNullOrEmpty()) {
                dialog.dismiss()
                showMessage(getString(R.string.insira_nome))
            } else {
                val firebaseToken = getFirebaseToken()
                with (color!!) {
                    if (num_codigo_online.isEmpty())
                         insert(it, firebaseToken)
                    else update(it, firebaseToken)
                }
            }
        }
        dialog.show(fragmentManager!!)
    }

    private fun insert(color: Color, firebaseToken: String) {
        with (viewModel) {
            insert(color, firebaseToken)
            insertResponse.observe(this@ColorFragment, observerHandler({
                add(it)
                dialog.dismiss()
            }, {
                showMessage(it, R.string.falha_inserir_cor)
            }))
        }
    }

    private fun update(colorToUpdate: Color, firebaseToken: String) {
        with (viewModel) {
            insert(colorToUpdate, firebaseToken)
            updateResponse.observe(this@ColorFragment, observerHandler({
                color?.version = it.version
                updateList(color!!)
                dialog.dismiss()
            }, {
                showMessage(it, R.string.falha_atualizar_cor)
            }))
        }
    }

}
