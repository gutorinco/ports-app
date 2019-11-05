package br.com.suitesistemas.portsmobile.model

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.entity.Customer
import com.google.gson.Gson

class UserResponse() : Parcelable {

    var codigo: Int = 0
    var usuario: String = ""
    var empresa: String = ""
    var area: String = ""
    var pessoa = Customer()
    var permissoes = Permissions()

    constructor(sharedPreferences: SharedPreferences): this() {
        val gson = Gson()
        codigo = sharedPreferences.getInt("codigo", 0)
        empresa = sharedPreferences.getString("empresa", "")!!
        usuario = sharedPreferences.getString("usuario", "")!!
        area = sharedPreferences.getString("area", "")!!

        val pessoaJson = sharedPreferences.getString("pessoa", "")
        if (!pessoaJson.isNullOrEmpty())
            pessoa = gson.fromJson(pessoaJson, Customer::class.java)

        val permissoesJson = sharedPreferences.getString("permissoes", "")
        if (!permissoesJson.isNullOrEmpty())
            permissoes = gson.fromJson(permissoesJson, Permissions::class.java)
    }

    constructor(parcel: Parcel) : this() {
        with (parcel.readBundle(javaClass.classLoader)!!) {
            codigo = getInt("codigo")
            usuario = getString("usuario") ?: ""
            empresa = getString("empresa") ?: ""
            area = getString("area") ?: ""
            pessoa = getParcelable("pessoa") ?: Customer()
            permissoes = getParcelable("permissoes") ?: Permissions()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with (Bundle()) {
            putInt("codigo", codigo)
            putString("usuario", usuario)
            putString("empresa", empresa)
            putString("area", area)
            putParcelable("pessoa", pessoa)
            putParcelable("permissoes", permissoes)
        }
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