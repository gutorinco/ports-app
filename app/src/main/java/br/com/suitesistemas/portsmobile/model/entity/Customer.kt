package br.com.suitesistemas.portsmobile.model.entity

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import br.com.suitesistemas.portsmobile.custom.json.ECustomerSituationDeserializer
import br.com.suitesistemas.portsmobile.model.enums.ECustomerSituation
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

class Customer() : Parcelable, ChangeableModel<Customer> {

    var num_codigo_online: String = ""
    var cod_pessoa: Int? = null
    var dsc_referencia: String? = null
    var dsc_nome_pessoa: String = ""
    var dsc_nome_contato: String? = ""
    var dsc_cpf_cnpj: String? = ""
    var dsc_rg_insc_estadual: String? = ""
    var dsc_ddd_01: String? = ""
    var dsc_fone_01: String? = ""
    var dsc_ddd_celular_01: String? = ""
    var dsc_celular_01: String? = ""
    var dsc_email: String? = ""
    var dsc_observacao: String? = ""
    var dsc_cidade: String? = ""
    var flg_cliente: EYesNo = EYesNo.S
    var flg_fornecedor: EYesNo = EYesNo.N
    var flg_representante: EYesNo = EYesNo.N
    var flg_funcionario: EYesNo = EYesNo.N
    var flg_usuario: EYesNo = EYesNo.N
    var flg_outro: EYesNo = EYesNo.N
    @JsonDeserialize(using = ECustomerSituationDeserializer::class)
    var flg_situacao_cliente: ECustomerSituation = ECustomerSituation.A
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_integrado_online: EYesNo = EYesNo.N
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var fky_empresa: Company = Company()
    var version: Int = 0

    constructor(customer: Customer) : this() {
        copy(customer)
    }

    override fun copy(customer: Customer) {
        num_codigo_online = customer.num_codigo_online
        cod_pessoa = customer.cod_pessoa
        dsc_referencia = customer.dsc_referencia
        dsc_nome_pessoa = customer.dsc_nome_pessoa
        dsc_nome_contato = customer.dsc_nome_contato
        dsc_cpf_cnpj = customer.dsc_cpf_cnpj
        dsc_rg_insc_estadual = customer.dsc_rg_insc_estadual
        dsc_ddd_01 = customer.dsc_ddd_01
        dsc_fone_01 = customer.dsc_fone_01
        dsc_ddd_celular_01 = customer.dsc_ddd_celular_01
        dsc_celular_01 = customer.dsc_celular_01
        dsc_email = customer.dsc_email
        dsc_observacao = customer.dsc_observacao
        dsc_cidade = customer.dsc_cidade
        flg_cliente = customer.flg_cliente
        flg_fornecedor = customer.flg_fornecedor
        flg_representante = customer.flg_representante
        flg_funcionario = customer.flg_funcionario
        flg_usuario = customer.flg_usuario
        flg_outro = customer.flg_outro
        flg_situacao_cliente = customer.flg_situacao_cliente
        flg_cadastrado_app = customer.flg_cadastrado_app
        flg_integrado_online = customer.flg_integrado_online
        flg_cadastrado_online = customer.flg_cadastrado_online
        fky_empresa = customer.fky_empresa
        version = customer.version
    }

    constructor(parcel: Parcel) : this() {
        num_codigo_online = parcel.readString() ?: ""
        cod_pessoa = parcel.readValue(Int::class.java.classLoader) as? Int
        dsc_referencia = parcel.readString()
        dsc_nome_pessoa = parcel.readString() ?: ""
        dsc_nome_contato = parcel.readString()
        dsc_cpf_cnpj = parcel.readString()
        dsc_rg_insc_estadual = parcel.readString()
        dsc_ddd_01 = parcel.readString()
        dsc_fone_01 = parcel.readString()
        dsc_ddd_celular_01 = parcel.readString()
        dsc_celular_01 = parcel.readString()
        dsc_email = parcel.readString()
        dsc_observacao = parcel.readString()
        dsc_cidade = parcel.readString()
        flg_cliente = EYesNo.values()[parcel.readInt()]
        flg_fornecedor = EYesNo.values()[parcel.readInt()]
        flg_representante = EYesNo.values()[parcel.readInt()]
        flg_funcionario = EYesNo.values()[parcel.readInt()]
        flg_usuario = EYesNo.values()[parcel.readInt()]
        flg_outro = EYesNo.values()[parcel.readInt()]
        flg_situacao_cliente = ECustomerSituation.values()[parcel.readInt()]
        flg_cadastrado_app = EYesNo.values()[parcel.readInt()]
        flg_integrado_online = EYesNo.values()[parcel.readInt()]
        flg_cadastrado_online = EYesNo.values()[parcel.readInt()]
        fky_empresa = parcel.readParcelable(Company::class.java.classLoader) ?: Company()
        version = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num_codigo_online)
        parcel.writeValue(cod_pessoa)
        parcel.writeString(dsc_referencia)
        parcel.writeString(dsc_nome_pessoa)
        parcel.writeString(dsc_nome_contato)
        parcel.writeString(dsc_cpf_cnpj)
        parcel.writeString(dsc_rg_insc_estadual)
        parcel.writeString(dsc_ddd_01)
        parcel.writeString(dsc_fone_01)
        parcel.writeString(dsc_ddd_celular_01)
        parcel.writeString(dsc_celular_01)
        parcel.writeString(dsc_email)
        parcel.writeString(dsc_observacao)
        parcel.writeString(dsc_cidade)
        parcel.writeInt(flg_cliente.ordinal)
        parcel.writeInt(flg_fornecedor.ordinal)
        parcel.writeInt(flg_representante.ordinal)
        parcel.writeInt(flg_funcionario.ordinal)
        parcel.writeInt(flg_usuario.ordinal)
        parcel.writeInt(flg_outro.ordinal)
        parcel.writeInt(flg_situacao_cliente.ordinal)
        parcel.writeInt(flg_cadastrado_app.ordinal)
        parcel.writeInt(flg_integrado_online.ordinal)
        parcel.writeInt(flg_cadastrado_online.ordinal)
        parcel.writeParcelable(fky_empresa, flags)
        parcel.writeInt(version)
    }

    @SuppressLint("WrongConstant")
    override fun describeContents(): Int {
        return 4
    }

    companion object CREATOR : Parcelable.Creator<Customer> {
        override fun createFromParcel(parcel: Parcel): Customer {
            return Customer(parcel)
        }

        override fun newArray(size: Int): Array<Customer?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId() = num_codigo_online

    override fun equals(other: Any?): Boolean {
        return num_codigo_online == (other as Customer).num_codigo_online
    }

}