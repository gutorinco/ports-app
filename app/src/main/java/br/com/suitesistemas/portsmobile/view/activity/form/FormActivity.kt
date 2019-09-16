package br.com.suitesistemas.portsmobile.view.activity.form

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class FormActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    protected fun configureActionBar(titleId: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(titleId)
    }

}