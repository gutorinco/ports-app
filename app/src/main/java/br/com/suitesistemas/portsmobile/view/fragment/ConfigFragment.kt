package br.com.suitesistemas.portsmobile.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.model.enums.EConfigProductSearch
import br.com.suitesistemas.portsmobile.model.enums.ESystemType
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.viewModel.ConfigViewModel
import kotlinx.android.synthetic.main.fragment_config.*

class ConfigFragment : Fragment() {

    private var sharedPref: SharedPreferences? = null
    private lateinit var viewModel: ConfigViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_config, container, false)

        setTitle(R.string.configuracoes)
        sharedPref = activity?.getSharedPreferences("config", Context.MODE_PRIVATE)
        initViewModel()

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchConfigurations()
        configureForm()
    }

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(activity!!)
        viewModel = ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        viewModel.initRepositories(companyName)
    }

    private fun fetchConfigurations() {
        val type = sharedPref!!.getString("systemType", null)
        if (type == null) {
            config_progressbar.show()

            with (viewModel) {
                fetchConfigurations()
                response.observe(this@ConfigFragment, observerHandler({
                    if (it.isNotEmpty())
                        config.copy(it[0])
                    putConfiguration("systemType", config.flg_tipo_sistema.name)
                }, {
                    showMessage(config_layout, getString(R.string.nao_encontrou_configuracoes))
                }, {
                    config_progressbar.hide()
                }))
            }
        } else {
            viewModel.config.flg_tipo_sistema = ESystemType.valueOf(type)
            config_progressbar.hide()
        }
    }

    private fun configureForm() {
        configureProductSearchRadios()
    }

    private fun configureProductSearchRadios() {
        val key = "productSearchBy"
        val config = EConfigProductSearch.valueOf(getConfiguration(key, EConfigProductSearch.DESCRICAO.name))
        with (config_product_search) {
            check(when (config) {
                EConfigProductSearch.COD_BARRAS -> config_product_search_barcode.id
                EConfigProductSearch.CODIGO -> config_product_search_code.id
                EConfigProductSearch.DESCRICAO -> config_product_search_name.id
                EConfigProductSearch.REFERENCIA -> config_product_search_ref.id
            })
            setOnCheckedChangeListener { _, checkedId ->
                putConfiguration(key, when (checkedId) {
                    config_product_search_barcode.id -> EConfigProductSearch.COD_BARRAS.name
                    config_product_search_code.id -> EConfigProductSearch.CODIGO.name
                    config_product_search_name.id -> EConfigProductSearch.DESCRICAO.name
                    config_product_search_ref.id -> EConfigProductSearch.REFERENCIA.name
                    else -> EConfigProductSearch.DESCRICAO.name
                })
            }
        }
    }

    private fun getConfiguration(key: String, defaultValue: String): String {
        return if (sharedPref == null) {
            defaultValue
        } else {
            sharedPref!!.getString(key, defaultValue)!!
        }
    }

    private fun putConfiguration(key: String, value: String) {
        sharedPref?.let {
            with(it.edit()) {
                putString(key, value)
                apply()
                commit()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (config_product_search.checkedRadioButtonId == -1)
            putConfiguration("productSearchBy", EConfigProductSearch.DESCRICAO.name)
    }

}
