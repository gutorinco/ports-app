package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
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

class Sale() : Parcelable, ChangeableModel<Sale> {

    var num_codigo_online: String = ""
    var cod_venda: Int? = null

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_emissao: Date = Date()

    @JsonProperty("hor_emissao")
    @JsonFormat(shape= JsonFormat.Shape.NUMBER)
    @JsonSerialize(using = DateSerializer::class)
    @JsonDeserialize(using = DateDeserializer::class)
    var hor_emissao: Date? = Date()

    var dbl_total_produtos: Double = 0.0
    var dbl_total_venda: Double = 0.0
    var dbl_valor_pago: Double = 0.0
    var dsc_observacao: String? = ""
    var fky_empresa: Company = Company()
    var fky_cliente: Customer = Customer()
    var fky_vendedor: Customer = Customer()
    var fky_condicao_pagamento: PaymentCondition = PaymentCondition()
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int = 0

    constructor(sale: Sale) : this() {
        copy(sale)
    }

    override fun copy(sale: Sale) {
        num_codigo_online = sale.num_codigo_online
        cod_venda = sale.cod_venda
        dat_emissao = sale.dat_emissao
        hor_emissao = sale.hor_emissao
        dbl_total_produtos = sale.dbl_total_produtos
        dbl_total_venda = sale.dbl_total_venda
        dbl_valor_pago = sale.dbl_valor_pago
        dsc_observacao = sale.dsc_observacao
        fky_empresa = sale.fky_empresa
        fky_cliente = sale.fky_cliente
        fky_vendedor = sale.fky_vendedor
        fky_condicao_pagamento = sale.fky_condicao_pagamento
        flg_cadastrado_app = sale.flg_cadastrado_app
        flg_cadastrado_online = sale.flg_cadastrado_online
        flg_integrado_online = sale.flg_integrado_online
        version = sale.version
    }

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        cod_venda = parcel.readValue(Int::class.java.classLoader) as? Int
        dat_emissao = Date(parcel.readLong())
        val hor = parcel.readLong()
        if (hor != Long.MIN_VALUE)
            hor_emissao = Date(hor)
        dbl_total_produtos = parcel.readDouble()
        dbl_total_venda = parcel.readDouble()
        dbl_valor_pago = parcel.readDouble()
        dsc_observacao = parcel.readString()
        fky_empresa = parcel.readParcelable(Company::class.java.classLoader) ?: Company()
        fky_cliente = parcel.readParcelable(Customer::class.java.classLoader) ?: Customer()
        fky_vendedor = parcel.readParcelable(Customer::class.java.classLoader) ?: Customer()
        fky_condicao_pagamento = parcel.readParcelable(PaymentCondition::class.java.classLoader) ?: PaymentCondition()
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeValue(cod_venda)
        parcel.writeLong(dat_emissao.time)
        if (hor_emissao != null)
             parcel.writeLong(hor_emissao!!.time)
        else parcel.writeLong(Long.MIN_VALUE)
        parcel.writeDouble(dbl_total_produtos)
        parcel.writeDouble(dbl_total_venda)
        parcel.writeDouble(dbl_valor_pago)
        parcel.writeString(dsc_observacao)
        parcel.writeParcelable(fky_empresa, flags)
        parcel.writeParcelable(fky_cliente, flags)
        parcel.writeParcelable(fky_vendedor, flags)
        parcel.writeParcelable(fky_condicao_pagamento, flags)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeInt(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 19
    }

    companion object CREATOR : Parcelable.Creator<Sale> {
        override fun createFromParcel(parcel: Parcel): Sale {
            return Sale(parcel)
        }

        override fun newArray(size: Int): Array<Sale?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId() = num_codigo_online

    override fun equals(other: Any?): Boolean {
        return num_codigo_online == (other as Sale).num_codigo_online
    }

}