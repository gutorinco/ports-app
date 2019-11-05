package br.com.suitesistemas.portsmobile.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.actionDoneClicked
import br.com.suitesistemas.portsmobile.custom.extensions.hideKeyboard
import br.com.suitesistemas.portsmobile.custom.extensions.hideProgressSpinner
import br.com.suitesistemas.portsmobile.custom.extensions.showProgressSpinner
import br.com.suitesistemas.portsmobile.model.UserRequest
import br.com.suitesistemas.portsmobile.model.UserResponse
import br.com.suitesistemas.portsmobile.service.auth.AuthRepository
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
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
            val user = UserResponse(it)
            if (user.empresa.isNotEmpty() && user.usuario.isNotEmpty() && user.area.isNotEmpty() &&
                user.codigo > 0 && user.area == "/app")
                    startMainActivity()
        }
    }

    private fun signIn(userRequest: UserRequest) {
        login_button.showProgressSpinner(R.string.entrando)

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
            login_button.hideProgressSpinner(R.string.entrar)
        })
    }

    private fun saveSession(userResponse: UserResponse) {
        sharedPref?.let {
            val gson = Gson()
            val pessoa = gson.toJson(userResponse.pessoa)
            val permissoes = gson.toJson(userResponse.permissoes)
            with (it.edit()) {
                putInt("codigo", userResponse.codigo)
                putString("empresa", userResponse.empresa)
                putString("usuario", userResponse.usuario)
                putString("area", userResponse.area)
                putString("pessoa", pessoa)
                putString("permissoes", permissoes)
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

}
