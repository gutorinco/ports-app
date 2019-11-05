package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.entity.key.ProductColorKey
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ProductColor() : Parcelable {

    var ids: ProductColorKey = ProductColorKey()
    var cod_produto: Product = Product()
    var cod_cor: Color = Color()
    var dsc_codigo_barras: String = ""
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_integrado_online: EYesNo = EYesNo.N
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var version: Int? = 0

    constructor(parcel: Parcel) : this() {
        cod_produto = parcel.readParcelable(Product::class.java.classLoader) ?: Product()
        cod_cor = parcel.readParcelable(Color::class.java.classLoader) ?: Color()
        dsc_codigo_barras = parcel.readString() ?: ""
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    constructor(product: Product = Product(), color: Color = Color()) : this() {
        cod_produto = product
        cod_cor = color
        ids.cod_produto = product.num_codigo_online
        ids.cod_cor = color.num_codigo_online
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(cod_produto, flags)
        parcel.writeParcelable(cod_cor, flags)
        parcel.writeString(dsc_codigo_barras)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeValue(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 18
    }

    companion object CREATOR : Parcelable.Creator<ProductColor> {
        override fun createFromParcel(parcel: Parcel): ProductColor {
            return ProductColor(parcel)
        }

        override fun newArray(size: Int): Array<ProductColor?> {
            return arrayOfNulls(size)
        }
    }

}