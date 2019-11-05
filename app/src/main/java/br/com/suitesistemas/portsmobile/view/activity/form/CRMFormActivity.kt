package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.databinding.ActivityCrmFormBinding
import br.com.suitesistemas.portsmobile.model.entity.CRM
import br.com.suitesistemas.portsmobile.model.entity.Customer
import br.com.suitesistemas.portsmobile.model.entity.Order
import br.com.suitesistemas.portsmobile.model.entity.Sale
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.IconUtils
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.activity.search.OrderSearchActivity
import br.com.suitesistemas.portsmobile.view.activity.search.PeopleSearchActivity
import br.com.suitesistemas.portsmobile.view.activity.search.SaleSearchActivity
import br.com.suitesistemas.portsmobile.view.adapter.OrderAdapter
import br.com.suitesistemas.portsmobile.view.adapter.SaleAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.CRMFormViewModel
import kotlinx.android.synthetic.main.activity_crm_form.*
import java.util.*

class CRMFormActivity : FormActivity<CRM>() {

    companion object {
        private const val PEOPLE_SELECTED_CODE = 1
        private const val ORDER_SELECTED_CODE = 2
        private const val SALE_SELECTED_CODE = 3
    }
    private lateinit var viewModel: CRMFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crm_form)

        configureActionBar(R.string.cadastro_crm)
        initViewModel()
        configureDataBinding()
    }

    override fun onResume() {
        super.onResume()
        configureForm()
        configureButtons()

        hideProgress()
    }

    override fun getBtnConfirm() = crm_form_btn_confirm
    override fun getProgressBar() = crm_form_progressbar
    override fun getLayout() = crm_form

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(CRMFormViewModel::class.java)
        viewModel.initRepository(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityCrmFormBinding>(this, R.layout.activity_crm_form)
            .apply {
                lifecycleOwner = this@CRMFormActivity
                viewModel = this@CRMFormActivity.viewModel
            }
    }

    private fun configureForm() {
        val crmToEdit = intent.getParcelableExtra<CRM>("crm")
        if (crmToEdit != null)
            edit(crmToEdit as CRM)
        crm_form_date.setText(viewModel.crm.value!!.dat_cadastro.toStringFormat())
        configureOccurenceDateField()
        configureSolutionDateField()
        configureCustomerField()
        configureAdapters()
    }

    private fun configureOccurenceDateField() {
        with (viewModel.crm.value!!) {
            with (crm_form_occurence_date) {
                val datePicker = getDatePickerDialog(dat_ocorrencia) {
                    dat_ocorrencia = it.time
                    setText(dat_ocorrencia.toStringFormat())
                }
                setText(dat_ocorrencia.toStringFormat())
                setOnClickListener { datePicker.show() }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) datePicker.show()
                }
            }
        }
    }

    private fun configureSolutionDateField() {
        with (viewModel.crm.value!!) {
            with (crm_form_solution_date) {
                val datePicker = getDatePickerDialog(dat_solucao ?: Date()) {
                    dat_solucao = it.time
                    setText(dat_solucao!!.toStringFormat())
                }
                if (dat_solucao != null)
                    setText(dat_solucao!!.toStringFormat())
                setOnClickListener { datePicker.show() }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) datePicker.show()
                }
            }
        }
    }

    private fun getDatePickerDialog(date: Date, dateSelected : (Calendar) -> Unit): DatePickerDialog {
        return DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            dateSelected(calendar)
        }, date.year(), date.month(), date.day())
    }

    private fun configureAdapters() {
        configureTypeSpinner()
        configureStatusSpinner()
    }

    private fun configureTypeSpinner() {
        with (viewModel) {
            val types = resources.getStringArray(R.array.crmTypes)
            val index = types.indexOfFirst {
                it == when (crm.value?.flg_tipo_crm) {
                    "A" -> "Atendimento"
                    "B" -> "Pós-venda"
                    else -> "Atendimento"
                }
            }
            crm_form_type.setAdapterAndSelection(this@CRMFormActivity, types.toList(), index)
            crm_form_type.onItemSelected {
                crm.value?.flg_tipo_crm = when (it) {
                    0 -> "A"
                    1 -> "B"
                    else -> "A"
                }
            }
        }
    }

    private fun configureStatusSpinner() {
        with (viewModel) {
            val status = resources.getStringArray(R.array.crmStatus)
            val index = status.indexOfFirst {
                it == when (crm.value?.int_status) {
                    1 -> "Em aberto"
                    2 -> "Concluído"
                    3 -> "Cancelado"
                    else -> "Em aberto"
                }
            }
            crm_form_status.setAdapterAndSelection(this@CRMFormActivity, status.toList(), index)
            crm_form_status.onItemSelected {
                crm.value?.int_status = it + 1
            }
        }
    }

    private fun configureCustomerField() {
        crm_form_customer.setOnClickListener { initCustomerSearchActivity() }
        crm_form_customer.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                initCustomerSearchActivity()
        }
    }

    private fun initCustomerSearchActivity() {
        executeAfterLoaded {
            val intent = Intent(this, PeopleSearchActivity::class.java)
            intent.putExtra("type", "C")
            startActivityForResult(intent, PEOPLE_SELECTED_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PEOPLE_SELECTED_CODE) {
            if (resultCode == Activity.RESULT_OK)
                data?.let {
                    val people = it.getParcelableExtra<Customer>("people_response")
                    crm_form_customer.setText(people.dsc_nome_pessoa)
                    viewModel.crm.value?.fky_cliente = people
                }
        } else if (requestCode == ORDER_SELECTED_CODE) {
            selectedItemHandler(resultCode, data) {
                orderSelected(it.getParcelableExtra("order_response"))
            }
        } else if (requestCode == SALE_SELECTED_CODE) {
            selectedItemHandler(resultCode, data) {
                saleSelected(it.getParcelableExtra("sale_response"))
            }
        }
        hideKeyboard()
    }

    private fun selectedItemHandler(resultCode: Int, data: Intent?, selected: (Intent) -> Unit) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let { selected(it) }
        } else {
            with (viewModel.crm.value!!) {
                fky_pedido = null
                fky_venda = null
            }
        }
        viewModel.checkListVisibility()
    }

    private fun orderSelected(order: Order) {
        with (viewModel.crm.value!!) {
            configureList(OrderAdapter(this@CRMFormActivity, mutableListOf(order)))
            fky_pedido = order
            fky_venda = null
            viewModel.label.value = "Pedido selecionado:"
        }
    }

    private fun saleSelected(sale: Sale) {
        with (viewModel.crm.value!!) {
            configureList(SaleAdapter(this@CRMFormActivity, mutableListOf(sale)))
            fky_venda = sale
            fky_pedido = null
            viewModel.label.value = "Venda selecionada:"
        }
    }

    private fun <K : RecyclerView.ViewHolder> configureList(customAdapter: Adapter<K>) {
        with (crm_form_item) {
            adapter = customAdapter
            addSwipe(SwipeToDeleteCallback(this@CRMFormActivity) { onClearItemSelection() })
        }
    }

    private fun onClearItemSelection() {
        with (viewModel) {
            crm.value?.fky_venda = null
            crm.value?.fky_pedido = null
            showList.value = false
        }
    }

    private fun configureButtons() {
        configureBtnAddOrder()
        configureBtnAddSale()
        configureBtnConfirm()
        crm_form_back_button.setOnClickListener { onClearItemSelection() }
    }

    private fun configureBtnAddOrder() {
        with (crm_form_add_order) {
            icon = IconUtils.get(this@CRMFormActivity, R.string.fa_dolly_solid, android.R.color.white, 18F)
            setOnClickListener {
                executeAfterLoaded {
                    val intent = Intent(this@CRMFormActivity, OrderSearchActivity::class.java)
                    intent.putExtra("get", true)
                    startActivityForResult(intent, ORDER_SELECTED_CODE)
                }
            }
        }
    }

    private fun configureBtnAddSale() {
        crm_form_add_sale.setOnClickListener {
            executeAfterLoaded {
                val intent = Intent(this@CRMFormActivity, SaleSearchActivity::class.java)
                intent.putExtra("get", true)
                startActivityForResult(intent, SALE_SELECTED_CODE)
            }
        }
    }

    private fun configureBtnConfirm() {
        crm_form_btn_confirm.setOnClickListener {
            executeAfterLoaded {
                try {
                    viewModel.validateForm()
                    showProgress()

                    val firebaseToken = FirebaseUtils.getToken(this)

                    if (viewModel.crm.value!!.num_codigo_online.isEmpty())
                         insert(firebaseToken)
                    else update(firebaseToken)
                } catch (ex: InvalidValueException) {
                    when (ex.field) {
                        "Cliente" -> crm_form_customer.error = ex.message!!
                        "Descricao" -> crm_form_description.error = ex.message!!
                        else -> showMessage(ex.message!!)
                    }
                }
            }
        }
    }

    private fun insert(firebaseToken: String) {
        with (viewModel) {
            insert(firebaseToken)
            insertResponse.observe(this@CRMFormActivity, observerHandler({
                crm.value = it
                created("crm_response", it)
            }, {
                showMessage(it, R.string.falha_inserir_crm)
            }, {
                hideProgress()
            }))
        }
    }

    private fun update(firebaseToken: String) {
        with (viewModel) {
            update(firebaseToken)
            updateResponse.observe(this@CRMFormActivity, observerHandler({
                crm.value!!.version = it.version
                created("crm_response", crm.value!!)
            }, {
                showMessage(it, R.string.falha_atualizar_crm)
            }, {
                hideProgress()
            }))
        }
    }

    private fun edit(crm: CRM) {
        with (viewModel) {
            concat(crm)
            checkListVisibility()
            crm_form_occurence_date.setText(crm.dat_ocorrencia.toStringFormat())
            crm_form_solution_date.setText(crm.dat_solucao?.toStringFormat())
            if (crm.fky_pedido != null) {
                orderSelected(crm.fky_pedido!!)
            } else if (crm.fky_venda != null) {
                saleSelected(crm.fky_venda!!)
            }
        }
    }

}
