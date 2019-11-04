package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class Combination() : Parcelable {

    var cod_combinacao: Int = 0
    var dsc_combinacao: String = ""
    var version: Int = 0

    constructor(parcel: Parcel) : this() {
        cod_combinacao = parcel.readInt()
        dsc_combinacao = parcel.readString() ?: ""
        version = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cod_combinacao)
        parcel.writeString(dsc_combinacao)
        parcel.writeInt(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 2
    }

    companion object CREATOR : Parcelable.Creator<Combination> {
        override fun createFromParcel(parcel: Parcel): Combination {
            return Combination(parcel)
        }

        override fun newArray(size: Int): Array<Combination?> {
            return arrayOfNulls(size)
        }
    }

}