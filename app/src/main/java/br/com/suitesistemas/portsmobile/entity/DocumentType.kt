package br.com.suitesistemas.portsmobile.entity

import android.os.Parcel
import android.os.Parcelable

class DocumentType(): Parcelable {

    var cod_tipo_documento: Int? = 0
    var dsc_tipo_documento: String? = ""
    var version: Int? = 0

    constructor(parcel: Parcel) : this() {
        cod_tipo_documento = parcel.readValue(Int::class.java.classLoader) as? Int
        dsc_tipo_documento = parcel.readString()
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(cod_tipo_documento)
        parcel.writeString(dsc_tipo_documento)
        parcel.writeValue(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DocumentType> {
        override fun createFromParcel(parcel: Parcel): DocumentType {
            return DocumentType(parcel)
        }

        override fun newArray(size: Int): Array<DocumentType?> {
            return arrayOfNulls(size)
        }
    }


}