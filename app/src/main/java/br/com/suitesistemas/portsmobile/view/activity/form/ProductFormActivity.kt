package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.databinding.ActivityProductFormBinding
import br.com.suitesistemas.portsmobile.model.entity.Color
import br.com.suitesistemas.portsmobile.model.entity.Configuration
import br.com.suitesistemas.portsmobile.model.entity.Product
import br.com.suitesistemas.portsmobile.model.enums.ESystemType
import br.com.suitesistemas.portsmobile.utils.DoubleUtils
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.SelectColorSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ColorAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.ProductFormViewModel
import kotlinx.android.synthetic.main.activity_product_form.*
import kotlin.math.roundToLong

class ProductFormActivity : FormActivity<Product>(R.menu.menu_product_form, R.id.menu_search_colors) {

    private lateinit var colorAdapter: ColorAdapter
    private lateinit var viewModel: ProductFormViewModel
    companion object {
        private const val COLORS_SELECTED = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_form)

        configureActionBar(R.string.cadastro_produto)
        initViewModel()
        configureDataBinding()
    }

    override fun onResume() {
        super.onResume()
        fetchCompanies()
        fetchUnits()
        configureForm()
        configureList()
        configureBtnConfirm()
        initSystemType()
        val productToEdit = intent.getParcelableExtra<Product>("product")
        if (productToEdit != null) {
            edit(productToEdit)
        } else if (viewModel.systemType == ESystemType.O) {
            fetchNextCode()
        }
    }

    override fun getBtnConfirm() = product_form_btn_confirm
    override fun getProgressBar() = product_form_progressbar
    override fun getLayout() = product_form

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(ProductFormViewModel::class.java)
        viewModel.initRepositories(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityProductFormBinding>(this, R.layout.activity_product_form)
            .apply {
                lifecycleOwner = this@ProductFormActivity
                viewModel = this@ProductFormActivity.viewModel
                doubleUtils = DoubleUtils()
            }
    }

    private fun fetchCompanies() {
        with (viewModel) {
            fetchAllCompanies()
            companiesResponse.observe(this@ProductFormActivity, observerHandler({
                addAllCompanies(it)
                val companiesName = getCompaniesNames()
                val index = getCompanyIndex()
                product_form_company.setAdapterAndSelection(this@ProductFormActivity, companiesName, index)
            }, {
                showMessage(it, R.string.nao_encontrou_empresas)
            }))
        }
    }

    private fun fetchUnits() {
        with (viewModel) {
            fetchAllUnits()
            unitsResponse.observe(this@ProductFormActivity, observerHandler({
                addAllUnits(it)
                val unitsName = getUnitNames()
                val index = getUnitIndex()
                product_form_unit_measure.setAdapterAndSelection(this@ProductFormActivity, unitsName, index)
            }, {
                showMessage(it, R.string.nao_encontrou_unidades)
            }, {
                hideProgress()
            }))
        }
    }

    override fun onClickedMenu() {
        executeAfterLoaded {
            val intent = Intent(this, SelectColorSearchActivity::class.java)
            startActivityForResult(intent, COLORS_SELECTED)
        }
    }

    private fun configureForm() {
        with (product_form_buy_price) {
            addNumberMask()
            afterTextChanged { text -> calculateSalePrice(text, false, false) }
        }
        with (product_form_buy_price_financed) {
            addNumberMask()
            afterTextChanged { text -> calculateSalePrice(text, true, false) }
        }
        with (product_form_sale_perc_price) {
            addNumberMask()
            afterTextChanged { text -> calculateSalePrice(text, false, true) }
        }
        with (product_form_sale_perc_price_financed) {
            addNumberMask()
            afterTextChanged { text -> calculateSalePrice(text, true, true) }
        }
        with (product_form_sale_price) {
            addNumberMask()
            afterTextChanged { text -> calculatePercentual(text, false) }
        }
        with (product_form_sale_price_financed) {
            addNumberMask()
            afterTextChanged { text -> calculatePercentual(text, true) }
        }
        product_form_company.onItemSelected { viewModel.product.value!!.fky_empresa = viewModel.companies[it] }
        product_form_unit_measure.onItemSelected { viewModel.product.value!!.fky_unidade_medida= viewModel.units[it] }
    }

    private fun configureList() {
        colorAdapter = ColorAdapter(baseContext, viewModel.colors) { onRemove(it) }
        with (product_form_colors) {
            adapter = colorAdapter
            addSwipe(SwipeToDeleteCallback(baseContext) { onRemove(it) })
        }
        configureEmptyView()
    }

    private fun onRemove(position: Int) {
        with (viewModel) {
            removeColorBy(position)
            colorAdapter.notifyDataSetChanged()
            if (listIsEmpty())
                configureEmptyView()
        }
    }

    private fun configureEmptyView() {
        if (viewModel.listIsEmpty()) {
            product_form_empty_view.visibility = View.VISIBLE
            product_form_colors.visibility = View.GONE
        } else {
            product_form_empty_view.visibility = View.GONE
            product_form_colors.visibility = View.VISIBLE
        }
    }

    private fun fetchNextCode() {
        showProgress()
        with (viewModel) {
            fetchNextCode()
            codeResponse.observe(this@ProductFormActivity, observerHandler({
                code.value = it.next.toString()
            }, {
                showMessage(getString(R.string.nao_encontrou_codigo))
            }, {
                hideProgress()
            }))
        }
    }

    private fun configureBtnConfirm() {
        product_form_btn_confirm.setOnClickListener {
            executeAfterLoaded {
                hideKeyboard()
                product_form_name.error = null

                try {
                    with(viewModel.product.value!!) {
                        dbl_compra_vista = product_form_buy_price.toDoubleValue()
                        dbl_compra_prazo = product_form_buy_price_financed.toDoubleValue()
                        dbl_venda_vista = product_form_sale_price.toDoubleValue()
                        dbl_venda_prazo = product_form_sale_price_financed.toDoubleValue()

                        viewModel.validateForm()

                        showProgress()
                        val firebaseToken = FirebaseUtils.getToken(this@ProductFormActivity)

                        if (num_codigo_online.isEmpty())
                             insert(firebaseToken)
                        else update(firebaseToken)
                    }
                } catch (ex: InvalidValueException) {
                    when (ex.field) {
                        "Nome" -> product_form_name.error = ex.message
                        else -> showMessage(ex.message!!)
                    }
                }
            }
        }
    }

    private fun insert(firebaseToken: String) {
        with (viewModel) {
            insert(firebaseToken)
            insertResponse.observe(this@ProductFormActivity, observerHandler({
                product.value = it
                saveColors()
            }, {
                showMessage(it, R.string.falha_inserir_produto)
            }))
        }
    }

    private fun update(firebaseToken: String) {
        with (viewModel) {
            update(firebaseToken)
            updateResponse.observe(this@ProductFormActivity, observerHandler({
                product.value?.version = it.version
                saveColors()
            }, {
                showMessage(it, R.string.falha_atualizar_produto)
            }))
        }
    }

    private fun saveColors() {
        with (viewModel) {
            if (newProductColors.isNotEmpty()) {
                insertColors()
                productColorInsertResponse.observe(this@ProductFormActivity, observerHandler({
                    newProductColors.clear()
                    this@ProductFormActivity.deleteColors()
                }, {
                    handleError(it, R.string.falha_inserir_cores)
                }))
            } else {
                this@ProductFormActivity.deleteColors()
            }
        }
    }

    private fun deleteColors() {
        with (viewModel) {
            if (removedColors.isNotEmpty()) {
                deleteColors()
                productColorDeleteResponse.observe(this@ProductFormActivity, observerHandler({
                    removedColors.removeAt(0)
                    this@ProductFormActivity.deleteColors()
                }, {
                    showMessage(it, R.string.falha_remover_cores)
                }))
            } else {
                hideProgress()
                created("product_response", product.value!!)
            }
        }
    }

    private fun initSystemType() {
        val sharedPref = getSharedPreferences("config", Context.MODE_PRIVATE)
        val type = sharedPref!!.getString("systemType", null)
        if (type == null) {
            showProgress()
            with (viewModel) {
                fetchConfigurations()
                configResponse.observe(this@ProductFormActivity, observerHandler({
                    val config = if (it.isNotEmpty()) it[0] else Configuration()
                    systemType = config.flg_tipo_sistema
                    setCodeFieldVisibility()

                    with(sharedPref.edit()) {
                        putString("systemType", systemType.name)
                        apply()
                        commit()
                    }
                }, {
                    showMessage(getString(R.string.nao_encontrou_configuracoes))
                }, {
                    hideProgress()
                }))
            }
        } else {
            viewModel.systemType = ESystemType.valueOf(type)
            setCodeFieldVisibility()
        }
    }

    private fun setCodeFieldVisibility() {
        if (viewModel.systemType == ESystemType.O)
             product_form_code_layout.visibility = View.VISIBLE
        else product_form_code_layout.visibility = View.GONE
    }

    private fun edit(productToEdit: Product) {
        showProgress()
        with (viewModel) {
            concat(productToEdit)
            fetchAllColors()
            productColorResponse.observe(this@ProductFormActivity, observerHandler({
                addAllProductColors(it)
                configureList()
            }, {
                showMessage(it, R.string.nao_encontrou_cores)
            }, {
                hideProgress()
            }))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == COLORS_SELECTED) {
            if (resultCode == Activity.RESULT_OK) {
                data?.extras?.let { bundle ->
                    val colorsSelected = bundle.getParcelableArrayList<Color>("colors_selected")
                    viewModel.addSelectedColors(colorsSelected)
                    configureList()
                }
            }
        }
        hideKeyboard()
    }

    private fun calculateSalePrice(text: String, isFinanced: Boolean, isPercentual: Boolean) {
        val percentual: Double
        val purchasePrice: Double

        if (isPercentual) {
            percentual = text.toDoubleValue()
            purchasePrice = getPurchaseValue(isFinanced)
        } else {
            percentual = if (isFinanced)
                 product_form_sale_perc_price_financed.toDoubleValue()
            else product_form_sale_perc_price.toDoubleValue()
            purchasePrice = text.toDoubleValue()
        }

        val salePrice = (purchasePrice * percentual / 100) + purchasePrice

        if (salePrice != purchasePrice) {
            if (isFinanced) {
                product_form_sale_price_financed.setTextSafely(salePrice)
            } else {
                product_form_sale_price.setTextSafely(salePrice)
            }
        }
    }

    private fun calculatePercentual(text: String, isFinanced: Boolean) {
        val saleValue = text.toDoubleValue()
        val purchaseValue = getPurchaseValue(isFinanced)
        val value = saleValue - purchaseValue
        val percentual = value * 100 / purchaseValue

        if (!percentual.isNaN()) {
            val total = if (percentual < 0) 0.0 else (percentual * 10.0).roundToLong() / 10.0
            if (isFinanced)
                 product_form_sale_perc_price_financed.setTextSafely(total)
            else product_form_sale_perc_price.setTextSafely(total)
        }
    }

    private fun getPurchaseValue(isFinanced: Boolean) : Double {
        return if (isFinanced)
             product_form_buy_price_financed.toDoubleValue()
        else product_form_buy_price.toDoubleValue()
    }

}