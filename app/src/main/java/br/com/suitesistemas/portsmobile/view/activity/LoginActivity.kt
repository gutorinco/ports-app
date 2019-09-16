package br.com.suitesistemas.portsmobile.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.edit_text.actionDoneClicked
import br.com.suitesistemas.portsmobile.custom.view.hideKeyboard
import br.com.suitesistemas.portsmobile.model.UserRequest
import br.com.suitesistemas.portsmobile.model.UserResponse
import br.com.suitesistemas.portsmobile.service.auth.AuthRepository
import com.github.razir.progressbutton.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = getSharedPreferences("userResponse", Context.MODE_PRIVATE)

        hideActionBar()
        configureButton()
        signInIfExistsSession()
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    private fun configureButton() {
        bindProgressButton(login_button)
        with (login_button) {
            attachTextChangeAnimator {
                fadeInMills = 200
                fadeOutMills = 200
            }
            setOnClickListener(this@LoginActivity)
        }
        login_password.actionDoneClicked { onClick(it) }
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        val user = UserRequest(
            login_company.text.toString(),
            login_user.text.toString(),
            login_password.text.toString()
        )
        when {
            user.empresa.isEmpty() -> {
                login_company.error = getString(R.string.obrigatorio)
                return
            }
            user.usuario.isEmpty() -> {
                login_user.error = getString(R.string.obrigatorio)
                return
            }
            user.senha.isEmpty() -> {
                login_password.error = getString(R.string.obrigatorio)
                return
            }
        }
        signIn(user)
    }

    private fun signInIfExistsSession() {
        sharedPref?.let {
            val user = UserResponse()
            user.codigo = it.getInt("codigo", 0)
            user.empresa = it.getString("empresa", "")!!
            user.usuario = it.getString("usuario", "")!!
            user.area = it.getString("area", "")!!

            if (user.empresa.isNotEmpty() && user.usuario.isNotEmpty() && user.area.isNotEmpty() &&
                user.codigo > 0 && user.area == "/app")
                    startMainActivity()
        }
    }

    private fun signIn(userRequest: UserRequest) {
        showProgress()

        AuthRepository().signIn(userRequest, {
            it?.let { userResponse ->
                if (userResponse.area == "/app") {
                    saveSession(it)
                    startMainActivity()
                } else {
                    clearSession()
                }
            }
        }, {
            it?.let { messageError ->
                Log.e("LOGIN ERROR:", messageError)
                Snackbar.make(login, messageError, Snackbar.LENGTH_LONG).show()
            }
        }, {
            hideProgress()
        })
    }

    private fun saveSession(userResponse: UserResponse) {
        sharedPref?.let {
            with (it.edit()) {
                putInt("codigo", userResponse.codigo)
                putString("empresa", userResponse.empresa)
                putString("usuario", userResponse.usuario)
                putString("area", userResponse.area)
                apply()
                commit()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("CommitPrefEdits")
    private fun clearSession() {
        sharedPref?.edit()?.clear()
    }

    private fun showProgress() {
        with (login_button) {
            isEnabled = false
            background.setColorFilter(getCustomColor(R.color.success_disabled), PorterDuff.Mode.MULTIPLY)
            showProgress {
                buttonTextRes = R.string.entrando
                progressColorRes = android.R.color.white
                gravity = DrawableButton.GRAVITY_TEXT_END
            }
        }
    }

    private fun hideProgress() {
        with (login_button) {
            isEnabled = true
            background.setColorFilter(getCustomColor(R.color.success), PorterDuff.Mode.MULTIPLY)
            hideProgress(R.string.entrar)
        }
    }

    private fun getCustomColor(colorId: Int): Int {
        return ContextCompat.getColor(this@LoginActivity, colorId)
    }
}
