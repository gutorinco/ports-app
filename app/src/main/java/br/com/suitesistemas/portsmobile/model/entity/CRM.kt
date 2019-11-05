package br.com.suitesistemas.portsmobile.model.entity

import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

class CRM() : Parcelable, ChangeableModel<CRM> {

    var num_codigo_online: String = ""
    var cod_crm: Int? = null
    var dsc_crm: String = ""
    var flg_tipo_crm: String = "A" // A - Atendimento, B - Pós-venda
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_cadastro: Date = Date()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_ocorrencia: Date = Date()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_solucao: Date? = null
    var int_status: Int = 1 // 1 - Em Aberto, 2 - Concluido, 3 - Cancelado
    var mem_ocorrencia: String? = null
    var mem_solucao: String? = null
    var fky_pedido: Order? = null
    var fky_venda: Sale? = null
    var fky_cliente: Customer = Customer()
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int? = 0

    constructor(crm: CRM) : this() {
        copy(crm)
    }

    override fun copy(crm: CRM) {
        num_codigo_online = crm.num_codigo_online
        cod_crm = crm.cod_crm
        dsc_crm = crm.dsc_crm
        flg_tipo_crm = crm.flg_tipo_crm
        dat_cadastro = crm.dat_cadastro
        dat_ocorrencia = crm.dat_ocorrencia
        dat_solucao = crm.dat_solucao
        int_status = crm.int_status
        mem_ocorrencia = crm.mem_ocorrencia
        mem_solucao = crm.mem_solucao
        fky_pedido = crm.fky_pedido
        fky_venda = crm.fky_venda
        fky_cliente = crm.fky_cliente
        flg_cadastrado_app = crm.flg_cadastrado_app
        flg_cadastrado_online = crm.flg_cadastrado_online
        flg_integrado_online = crm.flg_integrado_online
        version = crm.version
    }

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        cod_crm = parcel.readValue(Int::class.java.classLoader) as? Int
        dsc_crm = parcel.readString() ?: ""
        flg_tipo_crm = parcel.readString() ?: ""
        dat_cadastro = Date(parcel.readLong())
        dat_ocorrencia = Date(parcel.readLong())
        val solution = parcel.readLong()
        if (solution != Long.MIN_VALUE)
            dat_solucao = Date(solution)
        int_status = parcel.readInt()
        mem_ocorrencia = parcel.readString()
        mem_solucao = parcel.readString()
        fky_pedido = parcel.readParcelable(Order::class.java.classLoader)
        fky_venda = parcel.readParcelable(Sale::class.java.classLoader)
        fky_cliente = parcel.readParcelable(Customer::class.java.classLoader) ?: Customer()
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        version = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeValue(cod_crm)
        parcel.writeString(dsc_crm)
        parcel.writeString(flg_tipo_crm)
        parcel.writeLong(dat_cadastro.time)
        parcel.writeLong(dat_ocorrencia.time)
        parcel.writeLong(dat_solucao?.time ?: Long.MIN_VALUE)
        parcel.writeInt(int_status)
        parcel.writeString(mem_ocorrencia)
        parcel.writeString(mem_solucao)
        parcel.writeParcelable(fky_pedido, flags)
        parcel.writeParcelable(fky_venda, flags)
        parcel.writeParcelable(fky_cliente, flags)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeValue(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CRM> {
        override fun createFromParcel(parcel: Parcel): CRM {
            return CRM(parcel)
        }

        override fun newArray(size: Int): Array<CRM?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId() = num_codigo_online

    override fun equals(other: Any?): Boolean {
        return num_codigo_online == (other as CRM).num_codigo_online
    }

}