package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class PaymentSituation() : Parcelable {

    var cod_situacao_pagamento: Int? = 0
    var dsc_situacao_pagamento: String? = ""
    var version: Int? = 0

    constructor(parcel: Parcel) : this() {
        cod_situacao_pagamento = parcel.readValue(Int::class.java.classLoader) as? Int
        dsc_situacao_pagamento = parcel.readString()
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(cod_situacao_pagamento)
        parcel.writeString(dsc_situacao_pagamento)
        parcel.writeValue(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 16
    }

    companion object CREATOR : Parcelable.Creator<PaymentSituation> {
        override fun createFromParcel(parcel: Parcel): PaymentSituation {
            return PaymentSituation(parcel)
        }

        override fun newArray(size: Int): Array<PaymentSituation?> {
            return arrayOfNulls(size)
        }
    }

}