package br.com.suitesistemas.portsmobile.view.activity.form

import android.view.MenuItem
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.suitesistemas.portsmobile.custom.button.hideToBottom
import br.com.suitesistemas.portsmobile.custom.button.showFromBottom
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.roundToLong

abstract class FormActivity : AppCompatActivity() {

    protected abstract fun getBtnConfirm(): FloatingActionButton

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        getBtnConfirm().showFromBottom()
    }

    override fun onPause() {
        super.onPause()
        getBtnConfirm().hideToBottom()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    protected fun configureActionBar(titleId: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(titleId)
    }

    protected fun getDoubleValueFrom(editText: EditText): Double {
        var value = 0.0
        val valueText = editText.text.toString()
        if (valueText.isNotEmpty())
            value = getDoubleValueFrom(valueText)
        return value
    }

    protected fun getDoubleValueFrom(valueText: String): Double {
        val value = valueText.toDouble()
        return (value * 10.0).roundToLong() / 10.0
    }

}