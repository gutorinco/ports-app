package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class Model() : Parcelable, ChangeableModel<Model> {

    var num_codigo_online: String = ""
    var cod_modelo: Int? = null
    var dsc_modelo: String = ""
    var dsc_referencia: String? = null
    var dbl_preco_unit_vista: Double = 0.0
    var dbl_preco_unit_prazo: Double = 0.0
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var fky_grade: Grid = Grid()
    var fky_empresa: Company = Company()
    var version: Int? = null

    constructor(model: Model) : this() {
        copy(model)
    }

    override fun copy(model: Model) {
        num_codigo_online = model.num_codigo_online
        cod_modelo = model.cod_modelo
        dsc_modelo = model.dsc_modelo
        dsc_referencia = model.dsc_referencia
        dbl_preco_unit_vista = model.dbl_preco_unit_vista
        dbl_preco_unit_prazo = model.dbl_preco_unit_prazo
        flg_cadastrado_app = model.flg_cadastrado_app
        flg_cadastrado_online = model.flg_cadastrado_online
        flg_integrado_online = model.flg_integrado_online
        fky_grade = model.fky_grade
        fky_empresa = model.fky_empresa
        version = model.version
    }

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        cod_modelo = parcel.readValue(Int::class.java.classLoader) as? Int
        dsc_modelo = parcel.readString() ?: ""
        dsc_referencia = parcel.readString()
        dbl_preco_unit_vista = parcel.readDouble()
        dbl_preco_unit_prazo = parcel.readDouble()
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        fky_grade = parcel.readParcelable(Grid::class.java.classLoader) ?: Grid()
        fky_empresa = parcel.readParcelable(Company::class.java.classLoader) ?: Company()
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeValue(cod_modelo)
        parcel.writeString(dsc_modelo)
        parcel.writeString(dsc_referencia)
        parcel.writeDouble(dbl_preco_unit_vista)
        parcel.writeDouble(dbl_preco_unit_prazo)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeParcelable(fky_grade, flags)
        parcel.writeParcelable(fky_empresa, flags)
        parcel.writeValue(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 9
    }

    companion object CREATOR : Parcelable.Creator<Model> {
        override fun createFromParcel(parcel: Parcel): Model {
            return Model(parcel)
        }

        override fun newArray(size: Int): Array<Model?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId() = num_codigo_online

    override fun equals(other: Any?): Boolean {
        return num_codigo_online == (other as Model).num_codigo_online
    }

}