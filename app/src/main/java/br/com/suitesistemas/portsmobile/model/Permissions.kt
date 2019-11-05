package br.com.suitesistemas.portsmobile.model

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class Permissions() : Parcelable {

    var flg_visualizar_venda: EYesNo = EYesNo.N
    var flg_visualizar_financeiro: EYesNo = EYesNo.N

    constructor(parcel: Parcel) : this() {
        with (parcel.readBundle(javaClass.classLoader)!!) {
            flg_visualizar_venda = EYesNo.valueOf(getString("flg_visualizar_venda") ?: "N")
            flg_visualizar_financeiro = EYesNo.valueOf(getString("flg_visualizar_financeiro") ?: "N")
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with (Bundle()) {
            putString("flg_visualizar_venda", flg_visualizar_venda.name)
            putString("flg_visualizar_financeiro", flg_visualizar_financeiro.name)
            parcel.writeBundle(this)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Permissions> {
        override fun createFromParcel(parcel: Parcel): Permissions {
            return Permissions(parcel)
        }

        override fun newArray(size: Int): Array<Permissions?> {
            return arrayOfNulls(size)
        }
    }
}