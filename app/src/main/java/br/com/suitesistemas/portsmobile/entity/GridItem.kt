package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.entity.key.GridItemKey

class GridItem() : Parcelable {

    var ids: GridItemKey = GridItemKey()
    var fky_grade: Grid = Grid()
    var dsc_numero: String = ""
    var dsc_numero_americano: String? = null
    var dsc_numero_europeu: String? = null
    var flg_par_medio: String = ""
    var dbl_quantidade: Double = 0.0
    var dbl_percentual: Double = 0.0

    constructor(parcel: Parcel) : this() {
        ids = parcel.readParcelable(GridItemKey::class.java.classLoader) ?: GridItemKey()
        fky_grade = parcel.readParcelable(Grid::class.java.classLoader) ?: Grid()
        dsc_numero = parcel.readString() ?: ""
        dsc_numero_americano = parcel.readString()
        dsc_numero_europeu = parcel.readString()
        flg_par_medio = parcel.readString() ?: ""
        dbl_quantidade = parcel.readDouble()
        dbl_percentual = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(ids, flags)
        parcel.writeParcelable(fky_grade, flags)
        parcel.writeString(dsc_numero)
        parcel.writeString(dsc_numero_americano)
        parcel.writeString(dsc_numero_europeu)
        parcel.writeString(flg_par_medio)
        parcel.writeDouble(dbl_quantidade)
        parcel.writeDouble(dbl_percentual)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 8
    }

    companion object CREATOR : Parcelable.Creator<GridItem> {
        override fun createFromParcel(parcel: Parcel): GridItem {
            return GridItem(parcel)
        }

        override fun newArray(size: Int): Array<GridItem?> {
            return arrayOfNulls(size)
        }
    }

}