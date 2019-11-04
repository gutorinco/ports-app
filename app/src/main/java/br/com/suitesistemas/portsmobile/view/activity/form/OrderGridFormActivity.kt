package br.com.suitesistemas.portsmobile.view.activity.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.custom.extensions.*
import br.com.suitesistemas.portsmobile.custom.recycler_view.SwipeToDeleteCallback
import br.com.suitesistemas.portsmobile.databinding.ActivityOrderGridFormBinding
import br.com.suitesistemas.portsmobile.entity.Combination
import br.com.suitesistemas.portsmobile.entity.OrderGridItem
import br.com.suitesistemas.portsmobile.entity.OrderItem
import br.com.suitesistemas.portsmobile.utils.SharedPreferencesUtils
import br.com.suitesistemas.portsmobile.view.adapter.OrderGridAdapter
import br.com.suitesistemas.portsmobile.viewModel.form.OrderGridFormViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_order_grid_form.*
import java.util.*

class OrderGridFormActivity : FormActivity<OrderItem>() {

    lateinit var viewModel: OrderGridFormViewModel
    lateinit var gridAdapter: OrderGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_grid_form)

        configureActionBar(R.string.cadastro_grade)
        initViewModel()
        configureDataBinding()
    }

    override fun onResume() {
        super.onResume()
        initParams()
        fetchGridItems()
        configureForm()
        configureButtons()
    }

    override fun getBtnConfirm(): FloatingActionButton = order_grid_form_btn_confirm
    override fun getProgressBar() = order_grid_form_progressbar
    override fun getLayout() = order_grid_form

    private fun initViewModel() {
        val companyName = SharedPreferencesUtils.getCompanyName(this)
        viewModel = ViewModelProviders.of(this).get(OrderGridFormViewModel::class.java)
        viewModel.initRepositories(companyName)
    }

    private fun configureDataBinding() {
        DataBindingUtil
            .setContentView<ActivityOrderGridFormBinding>(this, R.layout.activity_order_grid_form)
            .apply {
                lifecycleOwner = this@OrderGridFormActivity
                viewModel = this@OrderGridFormActivity.viewModel
            }
    }

    private fun initParams() {
        with(intent.extras) {
            with(viewModel) {
                val combinations = getParcelableArrayList<Combination>("combinations")
                val combinationsAdd = getParcelableArrayList<Combination>("combinationsAlreadyAdd")
                addCombinations(combinations, combinationsAdd)
                paymentCondition = getParcelable("paymentCondition")
                model.value = getParcelable("model")
                val item = getParcelable<OrderItem>("orderItem")
                val grids = getParcelableArrayList<OrderGridItem>("grids")
                if (item != null && grids != null) {
                    orderItem.copy(item)
                    addAllOrderGrids(grids)
                }
                order_grid_form_unit_price.setText(getUnitPrice().toStringValue())
            }
        }
    }

    private fun fetchGridItems() {
        showProgress()

        with(viewModel) {
            fetchGridItems()
            gridItemsResponse.observe(this@OrderGridFormActivity, observerHandler({
                addAllGridItems(it)
                configureSpinners()
                if (orderGrids.isNotEmpty())
                    edit()
            }, {
                showMessage(it, R.string.nao_encontrou_grade_modelo)
            }, {
                hideProgress()
            }))
        }
    }

    private fun configureSpinners() {
        configureCombinationAdapter()
        configureNumberAdapter()
    }

    private fun configureCombinationAdapter() {
        val combinationsName = viewModel.combinations.map { combination -> combination.dsc_combinacao }
        order_grid_form_combination.setAdapterAndSelection(this, combinationsName)
    }

    private fun configureNumberAdapter() {
        val numbers = viewModel.gridItems.map { item -> item.dsc_numero }
        order_grid_form_number.setAdapterAndSelection(this, numbers)
    }

    private fun configureForm() {
        configureQuantityField()
        configureBoxField()
        configureDiscountField()
    }

    private fun configureQuantityField() {
        order_grid_form_quantity.actionDoneClicked { handleAddButton() }
        order_grid_form_quantity.afterTextChanged { text ->
            if (text.isEmpty() || text.toDouble() < 0) order_grid_form_quantity.error =
                getString(R.string.maior_que_zero)
        }
    }

    private fun configureBoxField() {
        order_grid_form_box.afterTextChanged { text ->
            var boxValue = 0.0
            if (text.isNotEmpty()) boxValue = text.toDoubleValue()

            if (boxValue <= 0) order_grid_form_box.error = getString(R.string.maior_que_zero)
            val discount = order_grid_form_discount.toDoubleValue()
            calculateTotal(discount, boxValue)
        }
    }

    private fun configureDiscountField() {
        order_grid_form_discount.addNumberMask()
        order_grid_form_discount.afterTextChanged { text ->
            var discount = 0.0
            if (text.isNotEmpty()) discount = text.toDoubleValue()
            val boxValue = order_grid_form_box.toDoubleValue()
            calculateTotal(discount, boxValue)
        }
    }

    private fun calculateTotal(discount: Double, boxValue: Double) {
        with(viewModel) {
            calculateTotal(discount, boxValue.toInt())
            order_grid_form_subtotal.setText(orderItem.dbl_total_item.toStringValue())
        }
    }

    private fun configureButtons() {
        configureAddButton()
        configureBtnConfirm()
    }

    private fun configureAddButton() {
        order_grid_form_add.setOnClickListener {
            executeAfterLoaded { handleAddButton() }
        }
    }

    private fun handleAddButton() {
        try {
            if (order_grid_form_number.adapter.count == 0) {
                showMessage(getString(R.string.nenhum_numero))
            } else {
                with(viewModel) {
                    val numberPosition = order_grid_form_number.selectedItemPosition
                    val number = gridItems[numberPosition].dsc_numero
                    val combination = getCombination()
                    val boxValue = order_grid_form_box.toDoubleValue().toInt()
                    val quantity = order_grid_form_quantity.toDoubleValue()
                    val discount = order_grid_form_discount.toDoubleValue()

                    buildGrid(boxValue, number, quantity)
                    calculateTotal(discount, boxValue)
                    order_grid_form_subtotal.setText(orderItem.dbl_total_item.toStringValue())
                    configureList(combination)
                }
            }
        } catch (ex: InvalidValueException) {
            when (ex.field) {
                "Existe" -> showMessage(ex.message!!)
                "Caixa" -> order_grid_form_box.error = ex.message
                "Quantidade" -> order_grid_form_quantity.error = ex.message
            }
        }
    }

    private fun getCombination(): Combination {
        val combinationPosition = order_grid_form_combination.selectedItemPosition
        return viewModel.combinations[combinationPosition]
    }

    private fun configureList(combination: Combination) {
        gridAdapter = OrderGridAdapter(baseContext, viewModel.orderGrids, combination) { onRemove(it) }
        with(order_grid_form_items) {
            adapter = gridAdapter
            addSwipe(SwipeToDeleteCallback(baseContext) { onRemove(it) })
        }
    }

    private fun onRemove(position: Int) {
        viewModel.removeGridBy(position)
        gridAdapter.notifyDataSetChanged()
        calculateTotal(order_grid_form_discount.toDoubleValue(),
            order_grid_form_box.toDoubleValue())
    }

    private fun configureBtnConfirm() {
        order_grid_form_btn_confirm.setOnClickListener {
            executeAfterLoaded {
                if (viewModel.orderGrids.isEmpty()) {
                    showMessage(R.string.adicione_grades)
                } else {
                    with(viewModel) {
                        toCompleteOrderItem(getCombination(), order_grid_form_discount.toDoubleValue())
                        with(Intent()) {
                            with(Bundle()) {
                                putParcelableArrayList("grids", ArrayList(orderGrids))
                                putParcelableArrayList("removedGrids", ArrayList(removedGrids))
                                putParcelable("orderItem", orderItem)
                                putExtras(this)
                            }
                            setResult(Activity.RESULT_OK, this)
                        }
                    }
                    finish()
                }
            }
        }
    }

    private fun edit() {
        with(viewModel) {
            order_grid_form_combination.setSelection(getCombinationIndex())
            order_grid_form_unit_price.setText(orderItem.dbl_preco_unit.toStringValue())
            order_grid_form_subtotal.setText(orderItem.dbl_total_item.toStringValue())
            order_grid_form_discount.setTextSafely(orderItem.dbl_desconto ?: 0.0)
            order_grid_form_box.setTextSafely(orderGrids.first().int_caixa ?: 0)

            configureList(orderItem.fky_combinacao)
        }
    }

}
