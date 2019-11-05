package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.ESystemType

class Configuration() : Parcelable {

    var num_codigo_online: String = ""
    var flg_tipo_sistema: ESystemType = ESystemType.A
    var version: Int = 0

    constructor(config: Configuration) : this() {
        copy(config)
    }

    fun copy(config: Configuration) {
        num_codigo_online = config.num_codigo_online
        flg_tipo_sistema = config.flg_tipo_sistema
        version = config.version
    }

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        flg_tipo_sistema = ESystemType.values()[parcel.readInt()]
        version = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeInt(flg_tipo_sistema.ordinal)
        parcel.writeInt(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 2
    }

    companion object CREATOR : Parcelable.Creator<Configuration> {
        override fun createFromParcel(parcel: Parcel): Configuration {
            return Configuration(parcel)
        }

        override fun newArray(size: Int): Array<Configuration?> {
            return arrayOfNulls(size)
        }
    }

}