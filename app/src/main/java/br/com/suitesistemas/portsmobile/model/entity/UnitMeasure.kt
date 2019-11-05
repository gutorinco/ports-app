package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class UnitMeasure() : Parcelable {

    var cod_unidade_medida: Int = 0
    var dsc_unidade_medida: String = ""
    var version: Int = 0

    constructor(parcel: Parcel) : this() {
        cod_unidade_medida = parcel.readInt()
        dsc_unidade_medida = parcel.readString() ?: ""
        version = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cod_unidade_medida)
        parcel.writeString(dsc_unidade_medida)
        parcel.writeInt(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 22
    }

    companion object CREATOR : Parcelable.Creator<UnitMeasure> {
        override fun createFromParcel(parcel: Parcel): UnitMeasure {
            return UnitMeasure(parcel)
        }

        override fun newArray(size: Int): Array<UnitMeasure?> {
            return arrayOfNulls(size)
        }
    }

}