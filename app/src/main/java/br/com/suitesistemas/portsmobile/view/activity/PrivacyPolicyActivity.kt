package br.com.suitesistemas.portsmobile.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import br.com.suitesistemas.portsmobile.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.politica_privacidade)

        privacy_policy_content.loadUrl("https://suitesistemas.com.br/politica-privacidade/")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
