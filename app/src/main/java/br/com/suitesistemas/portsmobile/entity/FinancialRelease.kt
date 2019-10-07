package br.com.suitesistemas.portsmobile.entity

import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.entity.key.FinancialReleaseKey
import br.com.suitesistemas.portsmobile.model.enums.EFinancialReleaseTypeOperation
import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

class FinancialRelease() : Parcelable {

    var ids: FinancialReleaseKey = FinancialReleaseKey()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_emissao: Date = Date()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_vencimento: Date = Date()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_pagamento: Date? = Date()
    var dbl_valor_total: Double = 0.0
    var dsc_observacao: String? = null
    var dsc_referencia: String = ""
    var flg_tipo_operacao: EFinancialReleaseTypeOperation = EFinancialReleaseTypeOperation.P
    var fky_empresa: Company = Company()
    var fky_pessoa: Customer = Customer()
    var fky_vendedor: Customer? = Customer()
    var fky_tipo_documento: DocumentType = DocumentType()
    var fky_situacao_pagamento: PaymentSituation = PaymentSituation()
    var version: Int = 0

    constructor(parcel: Parcel) : this() {
        dbl_valor_total = parcel.readDouble()
        dsc_observacao = parcel.readString()
        dsc_referencia = parcel.readString() ?: ""
        flg_tipo_operacao = EFinancialReleaseTypeOperation.values()[parcel.readInt()]
        fky_empresa = parcel.readParcelable(Company::class.java.classLoader) ?: Company()
        fky_pessoa = parcel.readParcelable(Customer::class.java.classLoader) ?: Customer()
        fky_vendedor = parcel.readParcelable(Customer::class.java.classLoader)
        fky_tipo_documento = parcel.readParcelable(DocumentType::class.java.classLoader) ?: DocumentType()
        version = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(dbl_valor_total)
        parcel.writeString(dsc_observacao)
        parcel.writeString(dsc_referencia)
        parcel.writeInt(flg_tipo_operacao.ordinal)
        parcel.writeParcelable(fky_empresa, flags)
        parcel.writeParcelable(fky_pessoa, flags)
        parcel.writeParcelable(fky_vendedor, flags)
        parcel.writeParcelable(fky_tipo_documento, flags)
        parcel.writeInt(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FinancialRelease> {
        override fun createFromParcel(parcel: Parcel): FinancialRelease {
            return FinancialRelease(parcel)
        }

        override fun newArray(size: Int): Array<FinancialRelease?> {
            return arrayOfNulls(size)
        }
    }

}