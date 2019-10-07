package br.com.suitesistemas.portsmobile.entity

import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class Grid() : Parcelable {

    var cod_grade: Int = 0
    var num_inicial: Int = 0
    var num_final: Int = 0
    var dsc_grade: String = ""
    var flg_administrador: EYesNo = EYesNo.N
    var flg_denominacao: String = ""
    var version: Int? = null

    constructor(parcel: Parcel) : this() {
        cod_grade = parcel.readInt()
        num_inicial = parcel.readInt()
        num_final = parcel.readInt()
        dsc_grade = parcel.readString() ?: ""
        flg_administrador = EYesNo.values()[parcel.readInt()]
        flg_denominacao = parcel.readString() ?: ""
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cod_grade)
        parcel.writeInt(num_inicial)
        parcel.writeInt(num_final)
        parcel.writeString(dsc_grade)
        parcel.writeInt(flg_administrador.ordinal)
        parcel.writeString(flg_denominacao)
        parcel.writeValue(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Grid> {
        override fun createFromParcel(parcel: Parcel): Grid {
            return Grid(parcel)
        }

        override fun newArray(size: Int): Array<Grid?> {
            return arrayOfNulls(size)
        }
    }
}