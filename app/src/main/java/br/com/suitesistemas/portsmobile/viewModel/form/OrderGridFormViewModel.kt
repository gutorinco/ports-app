package br.com.suitesistemas.portsmobile.viewModel.form

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.exception.InvalidValueException
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.*
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import br.com.suitesistemas.portsmobile.service.grid.item.GridItemRepository
import kotlin.math.roundToLong

class OrderGridFormViewModel(application: Application) : FormViewModel<Order>(application) {

    private lateinit var gridItemsRepository: GridItemRepository
    var gridItemsResponse = MutableLiveData<ApiResponse<MutableList<GridItem>?>>()
    val gridItems: MutableList<GridItem> = mutableListOf()
    val orderGrids: MutableList<OrderGridItem> = mutableListOf()
    val removedGrids: MutableList<OrderGridItem> = mutableListOf()
    val combinations: MutableList<Combination> = mutableListOf()
    var paymentCondition = PaymentCondition()
    val orderItem = OrderItem()
    @Bindable var model: MutableLiveData<Model> = MutableLiveData()
    @Bindable var listIsEmpty: MutableLiveData<Boolean> = MutableLiveData()

    init {
        model.value = Model()
        listIsEmpty.value = true
    }

    fun initRepositories(company: String) {
        gridItemsRepository = GridItemRepository(company)
    }

    fun fetchGridItems() {
        gridItemsResponse = gridItemsRepository.findAllBy(model.value!!.fky_grade.cod_grade)
    }

    fun addAllGridItems(items: List<GridItem>) {
        with(gridItems) {
            clear()
            addAll(items)
        }
    }

    fun addAllOrderGrids(grids: List<OrderGridItem>) {
        with(orderGrids) {
            clear()
            addAll(grids)
            listIsEmpty.value = grids.isEmpty()
        }
    }

    fun addCombinations(combinations: List<Combination>, combinationsAdd: List<Combination>?) {
        if (!combinations.isNullOrEmpty()) {
            if (combinationsAdd.isNullOrEmpty()) {
                this.combinations.addAll(combinations)
            } else {
                combinationsAdd.forEach {
                    this.combinations.addAll(combinations.filter { c -> c.cod_combinacao != it.cod_combinacao })
                }
            }
        }
    }

    fun getUnitPrice() : Double {
        return if (paymentCondition.flg_a_vista == EYesNo.S)
             model.value!!.dbl_preco_unit_vista
        else model.value!!.dbl_preco_unit_prazo
    }

    fun getCombinationIndex() : Int {
        val foundedCombinations = combinations.find {
            it.cod_combinacao == orderItem.fky_combinacao.cod_combinacao
        }
        foundedCombinations?.let {
            val combinationIndex = combinations.indexOf(it)
            return if (combinationIndex >= 0) combinationIndex - 1 else 0
        }
        return 0
    }

    fun calculateTotal(discount: Double, boxValue: Int) {
        with (orderItem) {
            dbl_total_item = if (boxValue <= 0) {
                0.0
            } else {
                val quantity = orderGrids.fold(0.0) { sum, el -> sum + el.dbl_quantidade }
                if (quantity >= 0) {
                    val unitPrice = getUnitPrice()
                    val value = unitPrice * quantity
                    val subtotal = (value * boxValue) - discount
                    (subtotal * 10.0).roundToLong() / 10.0
                } else {
                    0.0
                }
            }
        }
    }

    fun removeGridBy(position: Int) {
        val grid = orderGrids[position]
        removedGrids.add(grid)
        orderGrids.remove(grid)
        listIsEmpty.value = orderGrids.size == 0
    }

    fun buildGrid(box: Int, number: String, quantity: Double) {
        try {
            val foundGrid = orderGrids.find { it.ids.num_numero == number.toInt() }
            if (foundGrid == null) {
                if (box <= 0)
                    throw InvalidValueException("Caixa", getStringRes(R.string.maior_que_zero))
                if (quantity <= 0)
                    throw InvalidValueException("Quantidade", getStringRes(R.string.maior_que_zero))

                with(OrderGridItem()) {
                    ids.cod_sequencia = orderItem.cod_sequencia ?: 1
                    ids.fky_pedido = orderItem.fky_pedido?.num_codigo_online ?: ""
                    ids.num_numero = number.toInt()
                    dsc_numero = number
                    dbl_quantidade = quantity
                    int_caixa = box
                    orderGrids.add(this)
                }
            } else {
                throw InvalidValueException("Existe", getStringRes(R.string.grade_ja_adicionada))
            }
        } finally {
            listIsEmpty.value = orderGrids.size == 0
        }
    }

    fun toCompleteOrderItem(combination: Combination, discount: Double) {
        with (orderItem) {
            dbl_desconto = discount
            dbl_preco_unit = getUnitPrice()
            dbl_quantidade += orderGrids.fold(0) { sum, el -> if (sum > el.ids.cod_sequencia) sum else el.ids.cod_sequencia }
            flg_tipo_desconto = "V"
            fky_modelo = model.value!!
            fky_combinacao = combination
        }
    }

    fun separateRemovedGrids() {
        val grids: MutableList<OrderGridItem> = mutableListOf()
        with (removedGrids) {
            forEach { removed ->
                val notIncluded = orderGrids.find { it.ids.num_numero == removed.ids.num_numero } == null
                if (notIncluded)
                    grids.add(removed)
            }
            clear()
            addAll(grids)
        }
    }

}