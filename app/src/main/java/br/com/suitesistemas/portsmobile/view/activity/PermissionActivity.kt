package br.com.suitesistemas.portsmobile.view.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.suitesistemas.portsmobile.R
import kotlinx.android.synthetic.main.activity_permission.*

class PermissionActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        val icon = intent.getIntExtra("icon", R.drawable.ic_camera_accent)
        val name = intent.getStringExtra("name")

        permission_icon.setImageResource(icon)
        permission_name.text = name
        permission_button.setOnClickListener(this)
        hideActionBar()
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    override fun onClick(v: View?) {
        setResult(Activity.RESULT_OK)
        finish()
    }

}
