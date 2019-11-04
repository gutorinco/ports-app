package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class Company() : Parcelable {

    var cod_empresa: Int = 0
    var dsc_empresa: String = ""
    var dsc_apelido: String = ""
    var fky_pessoa: Int = 0
    val version: Int = 0

    constructor(parcel: Parcel) : this() {
        cod_empresa = parcel.readInt()
        dsc_empresa = parcel.readString() ?: ""
        dsc_apelido = parcel.readString() ?: ""
        fky_pessoa = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cod_empresa)
        parcel.writeString(dsc_empresa)
        parcel.writeString(dsc_apelido)
        parcel.writeInt(fky_pessoa)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 3
    }

    companion object CREATOR : Parcelable.Creator<Company> {
        override fun createFromParcel(parcel: Parcel): Company {
            return Company(parcel)
        }

        override fun newArray(size: Int): Array<Company?> {
            return arrayOfNulls(size)
        }
    }

}