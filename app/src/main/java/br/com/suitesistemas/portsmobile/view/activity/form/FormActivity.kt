package br.com.suitesistemas.portsmobile.view.activity.form

import androidx.appcompat.app.AppCompatActivity

abstract class FormActivity : AppCompatActivity() {

    protected fun configureActionBar(titleId: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(titleId)
    }

}