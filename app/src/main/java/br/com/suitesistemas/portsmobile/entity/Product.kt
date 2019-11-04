package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class Product() : Parcelable, ChangeableModel<Product> {

    var num_codigo_online: String = ""
    var cod_produto: Int? = 0
    var cod_online: Int = 0
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
        with (parcel.readBundle(javaClass.classLoader)!!) {
            num_codigo_online = getString("num_codigo_online") ?: ""
            cod_produto = getInt("cod_produto")
            cod_online = getInt("cod_online")
            dbl_compra_prazo = getDouble("dbl_compra_prazo")
            dbl_compra_vista = getDouble("dbl_compra_vista")
            dbl_venda_prazo = getDouble("dbl_venda_prazo")
            dbl_venda_vista = getDouble("dbl_venda_vista")
            dbl_perc_lucro_prazo = getDouble("dbl_perc_lucro_prazo")
            dbl_perc_lucro_vista = getDouble("dbl_perc_lucro_vista")
            dsc_observacao = getString("dsc_observacao")
            dsc_produto = getString("dsc_produto")
            dsc_referencia = getString("dsc_referencia")
            fky_empresa = getParcelable("fky_empresa")
            fky_unidade_medida = getParcelable("fky_unidade_medida")
            flg_cadastrado_app = EYesNo.valueOf(getString("flg_cadastrado_app") ?: "N")
            flg_cadastrado_online = EYesNo.valueOf(getString("flg_cadastrado_online") ?: "N")
            flg_integrado_online = EYesNo.valueOf(getString("flg_integrado_online") ?: "N")
            version = getInt("version")
        }
    }

    constructor(product: Product) : this() {
        copy(product)
    }

    override fun copy(product: Product) {
        num_codigo_online = product.num_codigo_online
        cod_produto = product.cod_produto
        cod_online = product.cod_online
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
        flg_cadastrado_online = product.flg_cadastrado_online
        flg_integrado_online = product.flg_integrado_online
        version = product.version
    }

    private fun getPrice(price: Double?) : Double {
        if (price == null || price.isNaN() || price < 0)
            return 0.0
        return price
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with (Bundle()) {
            putString("num_codigo_online", num_codigo_online)
            putInt("cod_produto", cod_produto ?: 0)
            putInt("cod_online", cod_online)
            putDouble("dbl_compra_prazo", dbl_compra_prazo ?: 0.0)
            putDouble("dbl_compra_vista", dbl_compra_vista ?: 0.0)
            putDouble("dbl_venda_prazo", dbl_venda_prazo ?: 0.0)
            putDouble("dbl_venda_vista", dbl_venda_vista ?: 0.0)
            putDouble("dbl_perc_lucro_prazo", dbl_perc_lucro_prazo ?: 0.0)
            putDouble("dbl_perc_lucro_vista", dbl_perc_lucro_vista ?: 0.0)
            putString("dsc_observacao", dsc_observacao)
            putString("dsc_produto", dsc_produto)
            putString("dsc_referencia", dsc_referencia)
            putParcelable("fky_empresa", fky_empresa)
            putParcelable("fky_unidade_medida", fky_unidade_medida)
            putString("flg_cadastrado_app", flg_cadastrado_app.name)
            putString("flg_cadastrado_online", flg_cadastrado_online.name)
            putString("flg_integrado_online", flg_integrado_online.name)
            putInt("version", version ?: 0)
            parcel.writeBundle(this)
        }
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 17
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId() = num_codigo_online

    override fun equals(other: Any?): Boolean {
        return num_codigo_online == (other as Product).num_codigo_online
    }

}