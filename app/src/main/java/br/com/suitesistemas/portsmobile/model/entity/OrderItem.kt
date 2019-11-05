package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class OrderItem() : Parcelable {

    var num_codigo_online: String = ""
    var fky_pedido: Order? = null
    var cod_sequencia: Int? = null
    var fky_empresa: Company = Company()
    var fky_modelo: Model = Model()
    var fky_combinacao: Combination = Combination()
    var dbl_desconto: Double? = 0.0
    var dbl_preco_unit: Double = 0.0
    var dbl_quantidade: Double = 0.0
    var dbl_total_item: Double = 0.0
    var dsc_observacao: String? = null
    var flg_tipo_desconto: String? = null
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int? = null

    constructor(orderItem: OrderItem) : this() {
        copy(orderItem)
    }

    fun copy(orderItem: OrderItem) {
        num_codigo_online = orderItem.num_codigo_online
        fky_pedido = orderItem.fky_pedido
        cod_sequencia = orderItem.cod_sequencia
        fky_empresa = orderItem.fky_empresa
        fky_modelo = orderItem.fky_modelo
        fky_combinacao = orderItem.fky_combinacao
        dbl_desconto = orderItem.dbl_desconto
        dbl_preco_unit = orderItem.dbl_preco_unit
        dbl_quantidade = orderItem.dbl_quantidade
        dbl_total_item = orderItem.dbl_total_item
        dsc_observacao = orderItem.dsc_observacao
        flg_tipo_desconto = orderItem.flg_tipo_desconto
        flg_integrado_online = orderItem.flg_integrado_online
        version = orderItem.version
    }

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        fky_pedido = parcel.readParcelable(Order::class.java.classLoader)
        cod_sequencia = parcel.readValue(Int::class.java.classLoader) as? Int
        fky_empresa = parcel.readParcelable(Company::class.java.classLoader) ?: Company()
        fky_modelo = parcel.readParcelable(Model::class.java.classLoader) ?: Model()
        fky_combinacao = parcel.readParcelable(Combination::class.java.classLoader) ?: Combination()
        dbl_desconto = parcel.readValue(Double::class.java.classLoader) as? Double
        dbl_preco_unit = parcel.readDouble()
        dbl_quantidade = parcel.readDouble()
        dbl_total_item = parcel.readDouble()
        dsc_observacao = parcel.readString()
        flg_tipo_desconto = parcel.readString()
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeParcelable(fky_pedido, flags)
        parcel.writeValue(cod_sequencia)
        parcel.writeParcelable(fky_empresa, flags)
        parcel.writeParcelable(fky_modelo, flags)
        parcel.writeParcelable(fky_combinacao, flags)
        parcel.writeValue(dbl_desconto)
        parcel.writeDouble(dbl_preco_unit)
        parcel.writeDouble(dbl_quantidade)
        parcel.writeDouble(dbl_total_item)
        parcel.writeString(dsc_observacao)
        parcel.writeString(flg_tipo_desconto)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeValue(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 14
    }

    companion object CREATOR : Parcelable.Creator<OrderItem> {
        override fun createFromParcel(parcel: Parcel): OrderItem {
            return OrderItem(parcel)
        }

        override fun newArray(size: Int): Array<OrderItem?> {
            return arrayOfNulls(size)
        }
    }

}