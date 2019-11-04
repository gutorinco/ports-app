package br.com.suitesistemas.portsmobile.view.fragment

import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.view.adapter.CustomAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_color.*

abstract class BasicFragment<T, K : CustomAdapter<T>> : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var layout: View
    protected lateinit var customAdapter: K
    protected companion object {
        const val CREATE_REQUEST_CODE = 1
        const val UPDATE_REQUEST_CODE = 2
        const val GET_REQUEST_CODE = 3
    }

    fun init(layout: View, adapter: K) {
        this.layout = layout
        this.customAdapter = adapter
    }

    protected abstract fun getFloatingButton(): FloatingActionButton
    protected abstract fun getProgressBar(): ProgressBar
    protected abstract fun getRefresh(): SwipeRefreshLayout
    protected abstract fun getLayout(): View

    abstract fun refresh()
    override fun onRefresh() {
        when (color_progressbar.isIndeterminate) {
            true -> color_refresh.isRefreshing = false
            false -> refresh()
        }
    }

    abstract fun deleteRollback()
    private fun deleted() {
        Handler().postDelayed({
            Snackbar.make(layout, getString(R.string.registro_removido), Snackbar.LENGTH_LONG)
                .setAction(R.string.desfazer) { deleteRollback() }
                .show()
        },250)
    }

    override fun onPause() {
        super.onPause()
        getFloatingButton().hideToBottom()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    abstract fun initSearchActivity()
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search_action -> {
                initSearchActivity()
                super.onOptionsItemSelected(item)
            }
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun onChangedResponse(data: List<T>?, operation: EHttpOperation, onGetOperation: () -> Unit) {
        when (operation) {
            EHttpOperation.GET -> onGetOperation()
            EHttpOperation.DELETE -> {
                deleted()
                setAdapter(data)
            }
            EHttpOperation.ROLLBACK -> {
                showMessage(layout, getString(R.string.acao_desfeita))
                setAdapter(data)
            }
            EHttpOperation.POST -> setAdapter(data)
            EHttpOperation.PUT -> setAdapter(data)
        }
    }

    private fun setAdapter(data: List<T>?) = data?.let { customAdapter.setAdapter(it) }

    protected fun getFirebaseToken() = FirebaseUtils.getToken(context!!)

    protected fun showMessage(message: String) {
        showMessage(getLayout(), message)
    }

    protected fun showMessage(messageId: Int) {
        showMessage(getLayout(), getString(messageId))
    }

    protected fun showMessage(errorMessage: String, clientMessage: Int) {
        showMessage(getLayout(), errorMessage, getString(clientMessage))
    }

    private fun isLoading() = getProgressBar().isIndeterminate

    protected fun showProgress() = getProgressBar().show()

    protected fun hideProgress() = getProgressBar().hide()

    protected open fun handleError(errorMessage: String, clientMessage: Int) {
        hideProgress()
        showMessage(getLayout(), errorMessage, getString(clientMessage))
    }

    protected fun executeAfterLoaded(execute: () -> Unit) {
        executeAfterLoaded(isLoading(), getLayout()) {
            execute()
        }
    }

}