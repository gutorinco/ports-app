package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.button.showFromBottom
import br.com.suitesistemas.portsmobile.custom.edit_text.afterTextChanged
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.observer.observerHandler
import br.com.suitesistemas.portsmobile.custom.progress_bar.hide
import br.com.suitesistemas.portsmobile.custom.progress_bar.show
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.custom.recycler_view.addSwipe
import br.com.suitesistemas.portsmobile.custom.spinner.setAdapterAndSelection
import br.com.suitesistemas.portsmobile.custom.view.executeAfterLoaded
import br.com.suitesistemas.portsmobile.custom.view.hideKeyboard
import br.com.suitesistemas.portsmobile.custom.view.showMessage
import br.com.suitesistemas.portsmobile.databinding.ActivityProductFormBinding
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.entity.Company
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.entity.UnitMeasure
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.SelectColorSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.ColorAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.ProductFormViewModel
import kotlinx.android.synthetic.main.activity_product_form.*
import kotlin.math.roundToLong

class ProductFormActivity : FormActivity() {

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
        configureObservers()
        configureDataBinding()
        configureForm()
        configureList()
        configureButtons()
        val productToEdit = intent.getParcelableExtra<Product>("product")
        if (productToEdit != null)
            edit(productToEdit)
    }

    override fun getBtnConfirm() = product_form_btn_confirm

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(ProductFormViewModel::class.java)
        viewModel.initRepositories(companyName)
        viewModel.fetchAllCompanies()
        viewModel.fetchAllUnits()
    }

    private fun configureObservers() {
        viewModel.companiesResponse.observe(this, observerHandler({
            configureCompanyAdapter(it)
        },{
            showMessage(product_form, it, getString(R.string.nao_encontrou_empresas))
        }))

        viewModel.unitsResponse.observe(this, observerHandler({
            configureUnitAdapter(it)
        }, {
            showMessage(product_form, it, getString(R.string.nao_encontrou_unidades))
        }, {
            product_form_progressbar.hide()
        }))
    }

    private fun configureCompanyAdapter(companies: MutableList<Company>) {
        viewModel.addAllCompanies(companies)
        val companiesName = companies.map { company -> company.dsc_empresa }
        val index = companies.indexOfFirst { company ->
            company.cod_empresa == viewModel.product.value?.fky_empresa?.cod_empresa
        }
        product_form_company.setAdapterAndSelection(this, companiesName, index)
    }

    private fun configureUnitAdapter(units: MutableList<UnitMeasure>) {
        viewModel.addAllUnits(units)
        val unitsName = units.map { unit -> unit.dsc_unidade_medida }
        val index = units.indexOfFirst { company ->
            company.cod_unidade_medida == viewModel.product.value?.fky_unidade_medida?.cod_unidade_medida
        }
        product_form_unit_measure.setAdapterAndSelection(this, unitsName, index)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityProductFormBinding>(this, R.layout.activity_product_form)
            .apply {
                lifecycleOwner = this@ProductFormActivity
                viewModel = this@ProductFormActivity.viewModel
            }
    }

    private fun configureForm() {
        product_form_buy_price.afterTextChanged { text -> calculateSalePrice(text, false, false) }
        product_form_buy_price_financed.afterTextChanged { text -> calculateSalePrice(text, true, false) }
        product_form_sale_perc_price.afterTextChanged { text -> calculateSalePrice(text, false, true) }
        product_form_sale_perc_price_financed.afterTextChanged { text -> calculateSalePrice(text, true, true) }
        product_form_sale_price.afterTextChanged { text -> calculatePercentual(text, false) }
        product_form_sale_price_financed.afterTextChanged { text -> calculatePercentual(text, true) }
    }

    private fun configureList() {
        colorAdapter = ColorAdapter(baseContext, viewModel.colors)
        product_form_colors.adapter = colorAdapter
        configureSwipe()
    }

    private fun configureSwipe() {
        product_form_colors.addSwipe(SwipeToDeleteCallback(baseContext) { position ->
            viewModel.removeColorBy(position)
            colorAdapter.notifyDataSetChanged()
        })
    }

    private fun configureButtons() {
        product_form_btn_confirm.setOnClickListener {
            executeAfterLoaded(product_form_progressbar.isIndeterminate, product_form) {
                onConfirm()
            }
        }
        product_form_btn_color.setOnClickListener {
            executeAfterLoaded(product_form_progressbar.isIndeterminate, product_form) {
                val intent = Intent(this, SelectColorSearchActivity::class.java)
                startActivityForResult(intent, COLORS_SELECTED)
            }
        }
    }

    private fun onConfirm() {
        hideKeyboard()
        product_form_name.error = null

        try {
            val unitPosition = product_form_unit_measure.selectedItemPosition
            val companyPosition = product_form_company.selectedItemPosition

            viewModel.product.value?.dbl_compra_vista = getDoubleValueFrom(product_form_buy_price)
            viewModel.product.value?.dbl_compra_prazo = getDoubleValueFrom(product_form_buy_price_financed)
            viewModel.product.value?.dbl_venda_vista = getDoubleValueFrom(product_form_sale_price)
            viewModel.product.value?.dbl_venda_prazo = getDoubleValueFrom(product_form_sale_price_financed)
            viewModel.validateForm(unitPosition, companyPosition)

            product_form_progressbar.show()
            val firebaseToken = FirebaseUtils.getToken(this)

            viewModel.save(firebaseToken)
            configureInsertObserver()
            configureUpdateObserver()
        } catch (ex: InvalidValueException) {
            when (ex.field) {
                "Nome" -> product_form_name.error = ex.message
                else -> showMessage(product_form, ex.message!!)
            }
        }
    }

    private fun configureInsertObserver() {
        viewModel.insertResponse.observe(this, observerHandler({
            viewModel.product.value = it
            saveColors()
        }, {
            showMessage(product_form, it, getString(R.string.falha_inserir_produto))
        }))
    }

    private fun configureUpdateObserver() {
        viewModel.updateResponse.observe(this, observerHandler({
            viewModel.product.value?.version = it.version
            saveColors()
        }, {
            showMessage(product_form, it, getString(R.string.falha_atualizar_produto))
        }))
    }

    private fun edit(productToEdit: Product) {
        product_form_progressbar.show()
        viewModel.concat(productToEdit)
        viewModel.fetchAllColors()
        viewModel.productColorResponse.observe(this, observerHandler({
            viewModel.addAllProductColors(it)
            configureList()
        }, {
            showMessage(product_form, it, getString(R.string.nao_encontrou_cores))
        }, {
            product_form_progressbar.hide()
        }))
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

    override fun onPostResume() {
        super.onPostResume()
        product_form_btn_confirm.showFromBottom()
    }

    private fun saveColors() {
        if (viewModel.newProductColors.isNotEmpty()) {
            viewModel.insertColors()
            viewModel.productColorInsertResponse.observe(this, observerHandler({
                viewModel.newProductColors.clear()
                deleteColors()
            }, {
                product_form_progressbar.hide()
                showMessage(product_form, it, getString(R.string.falha_inserir_cores))
            }))
        } else {
            deleteColors()
        }
    }

    private fun deleteColors() {
        if (viewModel.removedColors.isNotEmpty()) {
            product_form_progressbar.show()
            viewModel.deleteColors()
            viewModel.productColorDeleteResponse.observe(this, observerHandler({
                viewModel.removedColors.removeAt(0)
                deleteColors()
            }, {
                showMessage(product_form, it, getString(R.string.falha_remover_cores))
            }))
        } else {
            product_form_progressbar.hide()
            created(viewModel.product.value!!)
        }
    }

    private fun created(product: Product) {
        val data = Intent()
        data.putExtra("product_response", product)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun calculateSalePrice(text: String, isFinanced: Boolean, isPercentual: Boolean) {
        var percentual: Double
        var purchasePrice: Double

        if (isPercentual) {
            percentual = getTypedValue(text)
            purchasePrice = getPurchaseValue(isFinanced)
        } else {
            percentual = if (isFinanced)
                 getDoubleValueFrom(product_form_sale_perc_price_financed)
            else getDoubleValueFrom(product_form_sale_perc_price)
            purchasePrice = getTypedValue(text)
        }

        val salePrice = (purchasePrice * percentual / 100) + purchasePrice

        if (salePrice != purchasePrice) {
            if (isFinanced) {
                setTextRemovingListener(product_form_sale_price_financed, salePrice)
            } else {
                setTextRemovingListener(product_form_sale_price, salePrice)
            }
        }
    }

    private fun calculatePercentual(text: String, isFinanced: Boolean) {
        val saleValue = getTypedValue(text)
        val purchaseValue = getPurchaseValue(isFinanced)
        val value = saleValue - purchaseValue
        val percentual = value * 100 / purchaseValue

        if (!percentual.isNaN()) {
            val total = if (percentual < 0) 0.0 else (percentual * 10.0).roundToLong() / 10.0
            if (isFinanced)
                 setTextRemovingListener(product_form_sale_perc_price_financed, total)
            else setTextRemovingListener(product_form_sale_perc_price, total)
        }
    }

    private fun getTypedValue(text: String): Double {
        var value = 0.0
        if (text.isNotEmpty())
            value = getDoubleValueFrom(text)
        return value
    }

    private fun getPurchaseValue(isFinanced: Boolean) : Double {
        return if (isFinanced)
             getDoubleValueFrom(product_form_buy_price_financed)
        else getDoubleValueFrom(product_form_buy_price)
    }

    private fun setTextRemovingListener(editText: EditText, value: Double) {
        with (editText) {
            val watcher = getTag(R.id.textWatcher)
            if (watcher != null) {
                removeTextChangedListener(watcher as TextWatcher)
                setText(value.toString())
                addTextChangedListener(watcher)
            }
        }
    }

}