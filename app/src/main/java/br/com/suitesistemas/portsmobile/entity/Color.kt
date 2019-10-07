package br.com.suitesistemas.portsmobile.entity

import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class Color() : Parcelable {

    var num_codigo_online: String = ""
    var cod_cor: Int? = null
    var dsc_cor: String? = null
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_integrado_online: EYesNo = EYesNo.N
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var version: Int = 0

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        cod_cor = parcel.readValue(Int::class.java.classLoader) as? Int
        dsc_cor = parcel.readString()
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readInt()
    }

    constructor(name: String? = "") : this() {
        if (!name.isNullOrEmpty())
            dsc_cor = name
    }

    constructor(color: Color) : this() {
        copy(color)
    }

    fun copy(color: Color) {
        num_codigo_online = color.num_codigo_online
        cod_cor = color.cod_cor
        dsc_cor = color.dsc_cor
        flg_cadastrado_app = color.flg_cadastrado_app
        flg_integrado_online = color.flg_integrado_online
        flg_cadastrado_online = color.flg_cadastrado_online
        version = color.version
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeValue(cod_cor)
        parcel.writeString(dsc_cor)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeInt(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Color> {
        override fun createFromParcel(parcel: Parcel): Color {
            return Color(parcel)
        }

        override fun newArray(size: Int): Array<Color?> {
            return arrayOfNulls(size)
        }
    }

}