package br.com.suitesistemas.portsmobile.view.activity.search

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.toStringFormat
import com.google.android.material.snackbar.Snackbar
import java.util.*

abstract class SearchActivity :
        AppCompatActivity(),
        View.OnClickListener,
        TextView.OnEditorActionListener,
        DatePickerDialog.OnDateSetListener {

    private var deletedOnSearch = false
    private lateinit var layout: View
    private lateinit var query: TextView
    protected lateinit var dateDialog: DatePickerDialog

    protected fun init(layout: View) {
        this.layout = layout
    }

    protected fun hideActionBar() {
        supportActionBar?.hide()
    }

    protected fun initDatePicker(query: TextView, onCancel: () -> Unit) {
        val calendar = Calendar.getInstance()
        val date = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        dateDialog = DatePickerDialog(this, this, year, month, date)
        dateDialog.setOnCancelListener { onCancel() }
        this.query = query
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        query.text = calendar.toStringFormat()
        onClick(null)
    }

    protected fun onHomeNavigation() {
        if (!deletedOnSearch) {
            onBackPressed()
        } else {
            setResult(Activity.BIND_IMPORTANT, Intent())
            finish()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onClick(v)
            return true
        }
        return false
    }

    abstract fun deleteRollback()
    protected fun deleted() {
        deletedOnSearch = true
        Handler().postDelayed({
            Snackbar.make(layout, getString(R.string.registro_removido), Snackbar.LENGTH_LONG)
                .setAction(R.string.desfazer) { deleteRollback() }
                .show()
        },250)
    }

}