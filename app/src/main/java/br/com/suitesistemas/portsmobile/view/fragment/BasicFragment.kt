package br.com.suitesistemas.portsmobile.view.fragment

import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import br.com.suitesistemas.portsmobile.view.adapter.CustomAdapter
import com.google.android.material.snackbar.Snackbar

abstract class BasicFragment<T, K : CustomAdapter<T>> : Fragment() {

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

    abstract fun deleteRollback()
    private fun deleted() {
        Handler().postDelayed({
            Snackbar.make(layout, getString(R.string.registro_removido), Snackbar.LENGTH_LONG)
                .setAction(R.string.desfazer) { deleteRollback() }
                .show()
        },250)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    abstract fun initSearchActivity()
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null)
            return super.onOptionsItemSelected(item)
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

}