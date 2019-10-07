package br.com.suitesistemas.portsmobile.entity

import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.ETaskStatus
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import java.util.*

class Task() : Parcelable {

    var num_codigo_online: String = ""
    var cod_tarefa: Int? = null
    var dat_cadastro: Date = Date()
    var dat_inicio: Date? = null
    var dat_termino: Date? = null
    var dat_prev_inicio: Date? = null
    var dat_prev_termino: Date? = null
    var dsc_observacao: String? = null
    var dsc_tarefa: String = ""
    var flg_notificar: EYesNo = EYesNo.S
    var flg_status: ETaskStatus = ETaskStatus.A_INICIAR
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var fky_responsavel: Customer = Customer()
    var version: Int = 0

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        cod_tarefa = parcel.readValue(Int::class.java.classLoader) as? Int
        dsc_observacao = parcel.readString()
        dsc_tarefa = parcel.readString() ?: ""
        flg_notificar = EYesNo.values()[parcel.readInt()]
        flg_status = ETaskStatus.values()[parcel.readInt()]
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        fky_responsavel = parcel.readParcelable(Customer::class.java.classLoader) ?: Customer()
        version = parcel.readInt()
    }

    constructor(task: Task) : this() {
        copy(task)
    }

    fun copy(task: Task) {
        num_codigo_online = task.num_codigo_online
        cod_tarefa = task.cod_tarefa
        dat_cadastro = task.dat_cadastro
        dat_inicio = task.dat_inicio
        dat_termino = task.dat_termino
        dat_prev_inicio = task.dat_prev_inicio
        dat_prev_termino = task.dat_prev_termino
        dsc_observacao = task.dsc_observacao
        dsc_tarefa = task.dsc_tarefa
        flg_notificar = task.flg_notificar
        flg_status = task.flg_status
        flg_cadastrado_app = task.flg_cadastrado_app
        flg_integrado_online = task.flg_integrado_online
        flg_cadastrado_online = task.flg_cadastrado_online
        fky_responsavel = task.fky_responsavel
        version = task.version
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeValue(cod_tarefa)
        parcel.writeString(dsc_observacao)
        parcel.writeString(dsc_tarefa)
        parcel.writeInt(flg_notificar.ordinal)
        parcel.writeInt(flg_status.ordinal)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeParcelable(fky_responsavel, flags)
        parcel.writeInt(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }

}