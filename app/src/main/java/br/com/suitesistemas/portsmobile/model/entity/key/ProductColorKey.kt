package br.com.suitesistemas.portsmobile.model.entity.key

import android.os.Parcel
import android.os.Parcelable

class ProductColorKey() : Parcelable {

    var cod_produto: String = ""
    var cod_cor: String = ""

    constructor(parcel: Parcel) : this() {
        cod_produto = parcel.readString() ?: ""
        cod_cor = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cod_produto)
        parcel.writeString(cod_cor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductColorKey> {
        override fun createFromParcel(parcel: Parcel): ProductColorKey {
            return ProductColorKey(parcel)
        }

        override fun newArray(size: Int): Array<ProductColorKey?> {
            return arrayOfNulls(size)
        }
    }

}