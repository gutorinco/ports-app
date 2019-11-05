package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.custom.json.DateDeserializer
import br.com.suitesistemas.portsmobile.custom.json.DateSerializer
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*

class Order() : Parcelable, ChangeableModel<Order> {

    var num_codigo_online: String = ""
    var cod_pedido: Int? = null
    var dsc_observacao: String? = null

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_emissao: Date = Date()

    @JsonProperty("hor_emissao")
    @JsonFormat(shape= JsonFormat.Shape.NUMBER)
    @JsonSerialize(using = DateSerializer::class)
    @JsonDeserialize(using = DateDeserializer::class)
    var hor_emissao: Date? = Date()

    var dbl_desconto: Double? = 0.0
    var dbl_total_item: Double = 0.0
    var dbl_total_pedido: Double = 0.0
    var fky_cliente: Customer = Customer()
    var fky_representante: Customer? = null
    var fky_condicao_pagamento: PaymentCondition = PaymentCondition()
    var fky_empresa: Company = Company()
    var flg_tipo_desconto: String? = null
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int? = null

    constructor(order: Order) : this() {
        copy(order)
    }

    override fun copy(order: Order) {
        num_codigo_online = order.num_codigo_online
        cod_pedido = order.cod_pedido
        dsc_observacao = order.dsc_observacao
        dat_emissao = order.dat_emissao
        hor_emissao = order.hor_emissao
        dbl_desconto = order.dbl_desconto
        dbl_total_item = order.dbl_total_item
        dbl_total_pedido = order.dbl_total_pedido
        fky_cliente = order.fky_cliente
        fky_representante = order.fky_representante
        fky_condicao_pagamento = order.fky_condicao_pagamento
        fky_empresa = order.fky_empresa
        flg_tipo_desconto = order.flg_tipo_desconto
        flg_cadastrado_app = order.flg_cadastrado_app
        flg_cadastrado_online = order.flg_cadastrado_online
        flg_integrado_online = order.flg_integrado_online
        version = order.version
    }

    constructor(parcel: Parcel) : this() {
        with (parcel.readBundle(javaClass.classLoader)!!) {
            num_codigo_online = getString("num_codigo_online") ?: ""
            cod_pedido = getInt("cod_pedido")
            dsc_observacao = getString("dsc_observacao")
            dat_emissao = Date(getLong("dat_emissao"))
            val hor = getLong("hor_emissao")
            if (hor != 0.toLong())
                hor_emissao = Date(hor)
            dbl_desconto = getDouble("dbl_desconto")
            dbl_total_item = getDouble("dbl_total_item")
            dbl_total_pedido = getDouble("dbl_total_pedido")
            fky_cliente = getParcelable("fky_cliente") ?: Customer()
            fky_representante = getParcelable("fky_representante")
            fky_condicao_pagamento = getParcelable("fky_condicao_pagamento") ?: PaymentCondition()
            fky_empresa = getParcelable("fky_empresa") ?: Company()
            flg_tipo_desconto = getString("flg_tipo_desconto")
            flg_cadastrado_app = EYesNo.values()[getInt("flg_cadastrado_app")]
            flg_cadastrado_online = EYesNo.values()[getInt("flg_cadastrado_online")]
            flg_integrado_online = EYesNo.values()[getInt("flg_integrado_online")]
            version = getInt("version")
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with (Bundle()) {
            putString("num_codigo_online", num_codigo_online)
            putInt("cod_pedido", cod_pedido ?: 0)
            putString("dsc_observacao", dsc_observacao)
            putLong("dat_emissao", dat_emissao.time)
            val time = if (hor_emissao == null) 0.toLong() else hor_emissao!!.time
            putLong("hor_emissao", time)
            putDouble("dbl_desconto ", dbl_desconto ?: 0.0)
            putDouble("dbl_total_item", dbl_total_item)
            putDouble("dbl_total_pedido", dbl_total_pedido)
            putParcelable("fky_cliente", fky_cliente)
            putParcelable("fky_representante", fky_representante)
            putParcelable("fky_condicao_pagamento", fky_condicao_pagamento)
            putParcelable("fky_empresa", fky_empresa)
            putString("flg_tipo_desconto", flg_tipo_desconto)
            putInt("flg_cadastrado_app", flg_cadastrado_app.ordinal)
            putInt("flg_cadastrado_online", flg_cadastrado_online.ordinal)
            putInt("flg_integrado_online", flg_integrado_online.ordinal)
            putInt("version", version ?: 0)
            parcel.writeBundle(this)
        }
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 12
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order = Order(parcel)
        override fun newArray(size: Int): Array<Order?> = arrayOfNulls(size)
    }

    override fun getId() = num_codigo_online

    override fun equals(other: Any?): Boolean {
        return num_codigo_online == (other as Order).num_codigo_online
    }

}