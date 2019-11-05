package br.com.suitesistemas.portsmobile.model.entity.key

import android.os.Parcel
import android.os.Parcelable

class GridItemKey() : Parcelable {

    var fky_grade: Int = 0
    var num_numero: Int = 0

    constructor(parcel: Parcel) : this() {
        fky_grade = parcel.readInt()
        num_numero = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(fky_grade)
        parcel.writeInt(num_numero)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GridItemKey> {
        override fun createFromParcel(parcel: Parcel): GridItemKey {
            return GridItemKey(parcel)
        }

        override fun newArray(size: Int): Array<GridItemKey?> {
            return arrayOfNulls(size)
        }
    }

}