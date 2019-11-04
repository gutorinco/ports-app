package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Handler
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.suitesistemas.portsmobile.custom.extensions.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

abstract class FormActivity<T : Parcelable>(
        private val menuId: Int? = null,
        private val clickedMenuId: Int? = null
) : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    protected val calendar = Calendar.getInstance()
    protected lateinit var timeDialog: TimePickerDialog

    protected abstract fun getBtnConfirm(): FloatingActionButton
    protected abstract fun getProgressBar(): ProgressBar
    protected abstract fun getLayout(): View

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        Handler().postDelayed({ getLayout().requestFocus() }, 500)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        getBtnConfirm().showFromBottom()
    }

    override fun onPause() {
        super.onPause()
        getBtnConfirm().hideToBottom()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menuId == null)
            return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(menuId, menu)
        return true
    }

    open fun onClickedMenu() {
        throw RuntimeException("Not implemented!")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (menuId == null) {
            onBackPressed()
            return true
        } else {
            return when (item?.itemId) {
                clickedMenuId -> {
                    onClickedMenu()
                    false
                }
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> false
            }
        }
    }

    protected fun configureActionBar(titleId: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(titleId)
    }

    override fun onPostResume() {
        super.onPostResume()
        getBtnConfirm().showFromBottom()
    }

    protected fun created(responseKey: String, obj: T) {
        val data = Intent()
        data.putExtra(responseKey, obj)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    protected fun initTimeDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        timeDialog = TimePickerDialog(this, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        onTimeSet()
    }

    open fun onTimeSet() {
        throw RuntimeException("onTimeSet: Não implementado!")
    }

    protected fun showMessage(message: String) {
        showMessage(getLayout(), message)
    }

    protected fun showMessage(messageId: Int) {
        showMessage(getLayout(), getString(messageId))
    }

    protected fun showMessage(errorMessage: String, clientMessage: Int) {
        showMessage(getLayout(), errorMessage, getString(clientMessage))
    }

    protected fun isLoading() = getProgressBar().isIndeterminate

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