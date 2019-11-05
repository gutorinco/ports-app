package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class SaleItem() : Parcelable {

    var num_codigo_online: String = ""
    var cod_sequencia: Int = 0
    var cod_venda: Int? = 0
    var dbl_preco_unit: Double = 0.0
    var dbl_quantidade: Double = 0.0
    var dbl_total_item: Double = 0.0
    var dsc_observacao: String? = null
    var fky_produto: Product = Product()
    var fky_cor: Color = Color()
    var fky_unidade_medida: UnitMeasure = UnitMeasure()
    var fky_empresa: Company = Company()
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int? = null

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        cod_sequencia = parcel.readInt()
        cod_venda = parcel.readValue(Int::class.java.classLoader) as? Int
        dbl_preco_unit = parcel.readDouble()
        dbl_quantidade = parcel.readDouble()
        dbl_total_item = parcel.readDouble()
        dsc_observacao = parcel.readString()
        fky_produto = parcel.readParcelable(Product::class.java.classLoader) ?: Product()
        fky_cor = parcel.readParcelable(Color::class.java.classLoader) ?: Color()
        fky_unidade_medida = parcel.readParcelable(UnitMeasure::class.java.classLoader) ?: UnitMeasure()
        fky_empresa = parcel.readParcelable(Company::class.java.classLoader) ?: Company()
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeInt(cod_sequencia)
        parcel.writeValue(cod_venda)
        parcel.writeDouble(dbl_preco_unit)
        parcel.writeDouble(dbl_quantidade)
        parcel.writeDouble(dbl_total_item)
        parcel.writeString(dsc_observacao)
        parcel.writeParcelable(fky_produto, flags)
        parcel.writeParcelable(fky_cor, flags)
        parcel.writeParcelable(fky_unidade_medida, flags)
        parcel.writeParcelable(fky_empresa, flags)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeValue(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 20
    }

    companion object CREATOR : Parcelable.Creator<SaleItem> {
        override fun createFromParcel(parcel: Parcel): SaleItem {
            return SaleItem(parcel)
        }

        override fun newArray(size: Int): Array<SaleItem?> {
            return arrayOfNulls(size)
        }
    }

}