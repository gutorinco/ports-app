package br.com.suitesistemas.portsmobile.custom.view

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import com.google.android.material.snackbar.Snackbar

fun showMessage(view: View, message: Int) {
    Snackbar.make(view, view.resources.getString(message), Snackbar.LENGTH_LONG).show()
}
fun showMessage(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}
fun showMessage(view: View, receivedMessageError: String?, message: String) {
    if (receivedMessageError.isNullOrEmpty())
         showMessage(view, message)
    else showMessage(view, receivedMessageError)
}
fun showMessageError(view: View, tag: String, messageError: String?, customMessage: String) {
    Log.e(tag, messageError)
    Snackbar.make(view, customMessage, Snackbar.LENGTH_LONG).show()
}

fun Fragment.setTitle(titleId: Int) {
    activity?.setTitle(titleId)
}
fun Fragment.onChangedFailure(view: View, messageError: String, operation: EHttpOperation, finished: () -> Unit) {
    activity?.onChangedFailure(view, messageError, operation, finished)
}
fun Activity.onChangedFailure(view: View, messageError: String, operation: EHttpOperation, finished: () -> Unit) {
    baseContext.onChangedFailure(view, messageError, operation, finished)
}
fun Context.onChangedFailure(view: View, messageError: String, operation: EHttpOperation, finished: () -> Unit) {
    if (operation == EHttpOperation.ROLLBACK) {
        showMessageError(view, "ROLLBACK ERROR:", messageError, getString(R.string.falha_desfazer_acao))
    } else {
        showMessageError(view, "ERROR: ", messageError, messageError)
    }
    finished()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}
fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.executeAfterLoaded(isLoading: Boolean, view: View, execute: () -> Unit) {
    when (isLoading) {
        true -> showMessage(view, R.string.aguarde_terminar)
        false -> execute()
    }
}
fun Activity.executeAfterLoaded(isLoading: Boolean, view: View, execute: () -> Unit) {
    when (isLoading) {
        true -> showMessage(view, R.string.aguarde_terminar)
        false -> execute()
    }
}