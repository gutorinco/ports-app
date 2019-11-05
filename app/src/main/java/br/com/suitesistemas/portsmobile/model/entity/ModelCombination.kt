package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.entity.key.ModelCombinationKey
import br.com.suitesistemas.portsmobile.model.enums.EYesNo

class ModelCombination() : Parcelable {

    var ids: ModelCombinationKey = ModelCombinationKey()
    var cod_modelo: Model = Model()
    var cod_combinacao: Combination = Combination()
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int? = 0

    constructor(parcel: Parcel) : this() {
        cod_modelo = parcel.readParcelable(Model::class.java.classLoader) ?: Model()
        cod_combinacao = parcel.readParcelable(Combination::class.java.classLoader) ?: Combination()
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    constructor(model: Model = Model(), combination: Combination = Combination()) : this() {
        cod_modelo = model
        cod_combinacao = combination
        ids.cod_modelo = model.num_codigo_online
        ids.cod_combinacao = combination.cod_combinacao
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(cod_modelo, flags)
        parcel.writeParcelable(cod_combinacao, flags)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeValue(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 10
    }

    companion object CREATOR : Parcelable.Creator<ModelCombination> {
        override fun createFromParcel(parcel: Parcel): ModelCombination {
            return ModelCombination(parcel)
        }

        override fun newArray(size: Int): Array<ModelCombination?> {
            return arrayOfNulls(size)
        }
    }

}