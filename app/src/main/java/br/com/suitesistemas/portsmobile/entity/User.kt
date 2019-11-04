package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class User() : Parcelable {

    var cod_usuario: Int = 0
    var dsc_usuario: String? = ""
    var dsc_senha: String? = ""
    var dsc_token: String? = ""
    var fky_empresa: Company = Company()
    var fky_pessoa: Customer = Customer()
    var flg_tipo: String? = ""
    var flg_cadastrado_app: String? = ""
    var flg_cadastrado_online: String? = ""
    var flg_integrado_online: String? = ""
    var version: Int = 0

    constructor(parcel: Parcel) : this() {
        cod_usuario = parcel.readInt()
        dsc_usuario = parcel.readString()
        dsc_senha = parcel.readString()
        dsc_token = parcel.readString()
        fky_empresa = parcel.readParcelable(Company::class.java.classLoader) ?: Company()
        fky_pessoa = parcel.readParcelable(Customer::class.java.classLoader) ?: Customer()
        flg_tipo = parcel.readString()
        flg_cadastrado_app = parcel.readString()
        flg_cadastrado_online = parcel.readString()
        flg_integrado_online = parcel.readString()
        version = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cod_usuario)
        parcel.writeString(dsc_usuario)
        parcel.writeString(dsc_senha)
        parcel.writeString(dsc_token)
        parcel.writeParcelable(fky_empresa, flags)
        parcel.writeParcelable(fky_pessoa, flags)
        parcel.writeString(flg_tipo)
        parcel.writeString(flg_cadastrado_app)
        parcel.writeString(flg_cadastrado_online)
        parcel.writeString(flg_integrado_online)
        parcel.writeInt(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 23
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}