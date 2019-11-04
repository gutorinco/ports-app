package br.com.suitesistemas.portsmobile.entity.key

import android.os.Parcel
import android.os.Parcelable

class ModelGridCombinationKey() : Parcelable {

    var cod_modelo: String = ""
    var cod_combinacao: Int = 0
    var num_numero: Int = 0

    constructor(parcel: Parcel) : this() {
        cod_modelo = parcel.readString()
        cod_combinacao = parcel.readInt()
        num_numero = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cod_modelo)
        parcel.writeInt(cod_combinacao)
        parcel.writeInt(num_numero)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelGridCombinationKey> {
        override fun createFromParcel(parcel: Parcel): ModelGridCombinationKey {
            return ModelGridCombinationKey(parcel)
        }

        override fun newArray(size: Int): Array<ModelGridCombinationKey?> {
            return arrayOfNulls(size)
        }
    }

}