package br.com.suitesistemas.portsmobile.custom.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.EasyPermissions

fun showMessage(view: View, message: Int) {
    Snackbar.make(view, view.resources.getString(message), Snackbar.LENGTH_LONG).show()
}

fun showMessage(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun showMessage(view: View, receivedMessageError: String?, message: String) {
    if (receivedMessageError.isNullOrEmpty()) showMessage(view, message)
    else showMessageError(view, "ERROR", receivedMessageError, message)
}

fun showMessageError(view: View, tag: String, messageError: String?, customMessage: String) {
    Log.e(tag, messageError)
    Snackbar.make(view, customMessage, Snackbar.LENGTH_LONG).show()
}

fun Fragment.setTitle(titleId: Int) {
    activity?.setTitle(titleId)
}

fun Fragment.showMessageError(view: View, messageError: String, operation: EHttpOperation) {
    activity?.showMessageError(view, messageError, operation)
}

fun Activity.showMessageError(view: View, messageError: String, operation: EHttpOperation) {
    baseContext.showMessageError(view, messageError, operation)
}

fun Context.showMessageError(view: View, messageError: String, operation: EHttpOperation) {
    if (operation == EHttpOperation.ROLLBACK) {
        showMessageError(view,
            "ROLLBACK ERROR:",
            messageError,
            getString(R.string.falha_desfazer_acao))
    } else {
        showMessageError(view, "ERROR: ", messageError, messageError)
    }
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

fun Fragment.hasPermission(permissionName: String): Boolean {
    return EasyPermissions.hasPermissions(context!!, permissionName)
}

fun Activity.hasPermission(permissionName: String): Boolean {
    return EasyPermissions.hasPermissions(this, permissionName)
}

fun Fragment.requestPermission(permissionName: String, requestCode: Int = 123) {
    requestPermissions(arrayOf(permissionName), requestCode)
}

fun Activity.requestPermission(permissionName: String, requestCode: Int = 123) {
    ActivityCompat.requestPermissions(this, arrayOf(permissionName), requestCode)
}

fun Fragment.makeCall(phone: String, view: View) {
    makeCall(phone, view, activity!!)
}

fun Activity.makeCall(phone: String, view: View) {
    makeCall(phone, view, this)
}

private fun makeCall(phone: String, view: View, activity: Activity) {
    val uri = Uri.parse("tel:$phone")
    val intent = Intent(Intent.ACTION_DIAL, uri)
    if (intent.resolveActivity(activity.packageManager) != null)
        activity.startActivity(intent)
    else
        showMessage(view, R.string.acao_nao_suportada)
}

fun Fragment.sendEmail(email: String, view: View) {
    sendEmail(email, view, activity!!)
}

fun Activity.sendEmail(email: String, view: View) {
    sendEmail(email, view, this)
}

private fun sendEmail(email: String, view: View, activity: Activity) {
    val uri = Uri.parse("mailto:$email")
    val intent = Intent(Intent.ACTION_SENDTO, uri)
    if (intent.resolveActivity(activity.packageManager) != null)
        activity.startActivity(Intent.createChooser(intent, "Enviar email..."))
    else
        showMessage(view, R.string.acao_nao_suportada)
}