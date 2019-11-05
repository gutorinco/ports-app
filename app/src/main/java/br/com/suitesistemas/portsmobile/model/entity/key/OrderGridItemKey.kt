package br.com.suitesistemas.portsmobile.model.entity.key

import android.os.Parcel
import android.os.Parcelable

class OrderGridItemKey() : Parcelable {

    var fky_pedido: String = ""
    var cod_sequencia: Int = 0
    var num_numero: Int = 0

    constructor(parcel: Parcel) : this() {
        fky_pedido = parcel.readString() ?: ""
        cod_sequencia = parcel.readInt()
        num_numero = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fky_pedido)
        parcel.writeInt(cod_sequencia)
        parcel.writeInt(num_numero)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderGridItemKey> {
        override fun createFromParcel(parcel: Parcel): OrderGridItemKey {
            return OrderGridItemKey(parcel)
        }

        override fun newArray(size: Int): Array<OrderGridItemKey?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        val id = other as OrderGridItemKey
        return fky_pedido == id.fky_pedido &&
               num_numero == id.num_numero &&
               cod_sequencia == id.cod_sequencia
    }

}