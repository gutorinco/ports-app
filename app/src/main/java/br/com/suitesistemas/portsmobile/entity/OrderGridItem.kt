package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.entity.key.OrderGridItemKey
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class OrderGridItem() : Parcelable {

    var ids: OrderGridItemKey = OrderGridItemKey()
    var fky_pedido: Order = Order()
    var num_codigo_online: String = ""
    var dbl_quantidade: Double = 0.0
    var dsc_numero: String? = null
    var int_caixa: Int? = null
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int? = 0

    constructor(orderGrid: OrderGridItem) : this() {
        copy(orderGrid)
    }

    fun copy(orderGrid: OrderGridItem) {
        ids = orderGrid.ids
        fky_pedido = orderGrid.fky_pedido
        num_codigo_online = orderGrid.num_codigo_online
        dbl_quantidade = orderGrid.dbl_quantidade
        dsc_numero = orderGrid.dsc_numero
        int_caixa = orderGrid.int_caixa
        flg_cadastrado_app = orderGrid.flg_cadastrado_app
        flg_cadastrado_online = orderGrid.flg_cadastrado_online
        flg_integrado_online = orderGrid.flg_integrado_online
        version = orderGrid.version
    }

    constructor(parcel: Parcel) : this() {
        ids = parcel.readParcelable(OrderGridItemKey::class.java.classLoader) ?: OrderGridItemKey()
        fky_pedido = parcel.readParcelable(Order::class.java.classLoader) ?: Order()
        num_codigo_online = parcel.readString() ?: ""
        dbl_quantidade = parcel.readDouble()
        dsc_numero = parcel.readString()
        int_caixa = parcel.readValue(Int::class.java.classLoader) as? Int
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(ids, flags)
        parcel.writeParcelable(fky_pedido, flags)
        parcel.writeString(num_codigo_online)
        parcel.writeDouble(dbl_quantidade)
        parcel.writeString(dsc_numero)
        parcel.writeValue(int_caixa)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeValue(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int = 13

    companion object CREATOR : Parcelable.Creator<OrderGridItem> {
        override fun createFromParcel(parcel: Parcel): OrderGridItem = OrderGridItem(parcel)
        override fun newArray(size: Int): Array<OrderGridItem?> = arrayOfNulls(size)
    }

}