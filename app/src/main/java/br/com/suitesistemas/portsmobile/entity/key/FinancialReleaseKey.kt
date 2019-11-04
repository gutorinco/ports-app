package br.com.suitesistemas.portsmobile.entity.key

import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.custom.json.EFinancialReleaseStatusDeserializer
import br.com.suitesistemas.portsmobile.model.enums.EFinancialReleaseStatus
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

class FinancialReleaseKey() : Parcelable {

    var cod_lancamento_financeiro: Int = 0
    var cod_serie: Int = 0
    var cod_sequencia: Int = 0
    var fky_tipo_documento: Int = 0
    @JsonDeserialize(using = EFinancialReleaseStatusDeserializer::class)
    var flg_status: EFinancialReleaseStatus = EFinancialReleaseStatus.A

    constructor(parcel: Parcel) : this() {
        cod_lancamento_financeiro = parcel.readInt()
        cod_serie = parcel.readInt()
        cod_sequencia = parcel.readInt()
        fky_tipo_documento = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cod_lancamento_financeiro)
        parcel.writeInt(cod_serie)
        parcel.writeInt(cod_sequencia)
        parcel.writeInt(fky_tipo_documento)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FinancialReleaseKey> {
        override fun createFromParcel(parcel: Parcel): FinancialReleaseKey {
            return FinancialReleaseKey(parcel)
        }

        override fun newArray(size: Int): Array<FinancialReleaseKey?> {
            return arrayOfNulls(size)
        }
    }

}