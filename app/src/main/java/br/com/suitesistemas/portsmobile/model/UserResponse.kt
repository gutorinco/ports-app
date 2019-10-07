package br.com.suitesistemas.portsmobile.model

import android.os.Parcel
import android.os.Parcelable

class UserResponse() : Parcelable {

    var codigo: Int = 0
    var usuario: String = ""
    var empresa: String = ""
    var area: String = ""

    constructor(parcel: Parcel) : this() {
        codigo = parcel.readInt()
        usuario = parcel.readString() ?: ""
        empresa = parcel.readString() ?: ""
        area = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(codigo)
        parcel.writeString(usuario)
        parcel.writeString(empresa)
        parcel.writeString(area)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserResponse> {
        override fun createFromParcel(parcel: Parcel): UserResponse {
            return UserResponse(parcel)
        }

        override fun newArray(size: Int): Array<UserResponse?> {
            return arrayOfNulls(size)
        }
    }

}