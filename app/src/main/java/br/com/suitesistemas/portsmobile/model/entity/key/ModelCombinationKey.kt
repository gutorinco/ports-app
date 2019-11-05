package br.com.suitesistemas.portsmobile.model.entity.key

import android.os.Parcel
import android.os.Parcelable

class ModelCombinationKey() : Parcelable {

    var cod_modelo: String = ""
    var cod_combinacao: Int = 0

    constructor(parcel: Parcel) : this() {
        cod_modelo = parcel.readString() ?: ""
        cod_combinacao = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cod_modelo)
        parcel.writeInt(cod_combinacao)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelCombinationKey> {
        override fun createFromParcel(parcel: Parcel): ModelCombinationKey {
            return ModelCombinationKey(parcel)
        }

        override fun newArray(size: Int): Array<ModelCombinationKey?> {
            return arrayOfNulls(size)
        }
    }

}