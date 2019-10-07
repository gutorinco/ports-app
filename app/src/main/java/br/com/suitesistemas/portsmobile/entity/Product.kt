package br.com.suitesistemas.portsmobile.entity

import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class Product() : Parcelable {

    var num_codigo_online: String = ""
    var cod_produto: Int? = 0
    var dbl_compra_prazo: Double? = 0.0
    var dbl_compra_vista: Double? = 0.0
    var dbl_venda_prazo: Double? = 0.0
    var dbl_venda_vista: Double? = 0.0
    var dbl_perc_lucro_prazo: Double? = 0.0
    var dbl_perc_lucro_vista: Double? = 0.0
    var dsc_observacao: String? = ""
    var dsc_produto: String? = ""
    var dsc_referencia: String? = ""
    var fky_empresa: Company? = Company()
    var fky_unidade_medida: UnitMeasure? = UnitMeasure()
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_integrado_online: EYesNo = EYesNo.N
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var version: Int? = 0

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        cod_produto = parcel.readValue(Int::class.java.classLoader) as? Int
        dbl_compra_prazo = parcel.readValue(Double::class.java.classLoader) as? Double
        dbl_compra_vista = parcel.readValue(Double::class.java.classLoader) as? Double
        dbl_venda_prazo = parcel.readValue(Double::class.java.classLoader) as? Double
        dbl_venda_vista = parcel.readValue(Double::class.java.classLoader) as? Double
        dbl_perc_lucro_prazo = parcel.readValue(Double::class.java.classLoader) as? Double
        dbl_perc_lucro_vista = parcel.readValue(Double::class.java.classLoader) as? Double
        dsc_observacao = parcel.readString()
        dsc_produto = parcel.readString()
        dsc_referencia = parcel.readString()
        fky_empresa = parcel.readParcelable(Company::class.java.classLoader)
        fky_unidade_medida = parcel.readParcelable(UnitMeasure::class.java.classLoader)
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    constructor(product: Product) : this() {
        copy(product)
    }

    fun copy(product: Product) {
        num_codigo_online = product.num_codigo_online
        cod_produto = product.cod_produto
        dbl_compra_prazo = getPrice(product.dbl_compra_prazo)
        dbl_compra_vista = getPrice(product.dbl_compra_vista)
        dbl_venda_prazo = getPrice(product.dbl_venda_prazo)
        dbl_venda_vista = getPrice(product.dbl_venda_vista)
        dbl_perc_lucro_prazo = getPrice(product.dbl_perc_lucro_prazo)
        dbl_perc_lucro_vista = getPrice(product.dbl_perc_lucro_vista)
        dsc_observacao = product.dsc_observacao
        dsc_produto = product.dsc_produto
        dsc_referencia = product.dsc_referencia
        fky_empresa = product.fky_empresa
        fky_unidade_medida = product.fky_unidade_medida
        flg_cadastrado_app = product.flg_cadastrado_app
        flg_integrado_online = product.flg_integrado_online
        flg_cadastrado_online = product.flg_cadastrado_online
        version = product.version
    }

    private fun getPrice(price: Double?) : Double {
        if (price == null || price.isNaN() || price < 0)
            return 0.0
        return price
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeValue(cod_produto)
        parcel.writeValue(dbl_compra_prazo)
        parcel.writeValue(dbl_compra_vista)
        parcel.writeValue(dbl_venda_prazo)
        parcel.writeValue(dbl_venda_vista)
        parcel.writeValue(dbl_perc_lucro_prazo)
        parcel.writeValue(dbl_perc_lucro_vista)
        parcel.writeString(dsc_observacao)
        parcel.writeString(dsc_produto)
        parcel.writeString(dsc_referencia)
        parcel.writeParcelable(fky_empresa, flags)
        parcel.writeParcelable(fky_unidade_medida, flags)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeValue(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}