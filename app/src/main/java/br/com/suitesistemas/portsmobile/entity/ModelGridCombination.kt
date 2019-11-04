package br.com.suitesistemas.portsmobile.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.entity.key.ModelGridCombinationKey
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class ModelGridCombination() : Parcelable {

    var ids: ModelGridCombinationKey = ModelGridCombinationKey()
    var cod_modelo: Model = Model()
    var cod_combinacao: Combination = Combination()
    var dbl_qte_atual: Double = 0.0
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int? = null

    constructor(model: Model, combination: Combination, gridItem: GridItem) : this() {
        cod_modelo = model
        cod_combinacao = combination
        ids.cod_modelo = model.num_codigo_online
        ids.cod_combinacao = combination.cod_combinacao
        ids.num_numero = gridItem.ids.num_numero
        dbl_qte_atual = gridItem.dbl_quantidade
    }

    constructor(parcel: Parcel) : this() {
        ids = parcel.readParcelable(ModelGridCombinationKey::class.java.classLoader) ?: ModelGridCombinationKey()
        cod_modelo = parcel.readParcelable(Model::class.java.classLoader) ?: Model()
        cod_combinacao = parcel.readParcelable(Combination::class.java.classLoader) ?: Combination()
        dbl_qte_atual = parcel.readDouble()
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(ids, flags)
        parcel.writeParcelable(cod_modelo, flags)
        parcel.writeParcelable(cod_combinacao, flags)
        parcel.writeDouble(dbl_qte_atual)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeValue(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 11
    }

    companion object CREATOR : Parcelable.Creator<ModelGridCombination> {
        override fun createFromParcel(parcel: Parcel): ModelGridCombination {
            return ModelGridCombination(parcel)
        }

        override fun newArray(size: Int): Array<ModelGridCombination?> {
            return arrayOfNulls(size)
        }
    }

}