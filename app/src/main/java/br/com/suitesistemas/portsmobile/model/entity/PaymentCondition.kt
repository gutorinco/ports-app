package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class PaymentCondition() : Parcelable {

    var cod_condicao_pagamento: Int = 0
    var dsc_condicao_pagamento: String = ""
    var flg_a_vista = EYesNo.N
    var version: Int = 0

    constructor(parcel: Parcel) : this() {
        cod_condicao_pagamento = parcel.readInt()
        dsc_condicao_pagamento = parcel.readString() ?: ""
        flg_a_vista = EYesNo.values()[parcel.readInt()]
        version = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cod_condicao_pagamento)
        parcel.writeString(dsc_condicao_pagamento)
        parcel.writeInt(flg_a_vista.ordinal)
        parcel.writeInt(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 15
    }

    companion object CREATOR : Parcelable.Creator<PaymentCondition> {
        override fun createFromParcel(parcel: Parcel): PaymentCondition {
            return PaymentCondition(parcel)
        }

        override fun newArray(size: Int): Array<PaymentCondition?> {
            return arrayOfNulls(size)
        }
    }

}