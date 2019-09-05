package br.com.suitesistemas.portsmobile.entity

import br.com.suitesistemas.portsmobile.model.enums.ECustomerSituation
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import java.io.Serializable

class Customer : Serializable {

    var num_codigo_online: String = ""
    var cod_pessoa: Int? = null
    var dsc_referencia: String? = null
    var dsc_nome_pessoa: String = ""
    var dsc_nome_contato: String? = ""
    var dsc_cpf_cnpj: String? = ""
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
    var flg_situacao_cliente: ECustomerSituation = ECustomerSituation.A
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_integrado_online: EYesNo = EYesNo.N
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var fky_empresa: Company = Company()
    var version: Int = 0

    fun copy(customer: Customer) {
        num_codigo_online = customer.num_codigo_online
        cod_pessoa = customer.cod_pessoa
        dsc_referencia = customer.dsc_referencia
        dsc_nome_pessoa = customer.dsc_nome_pessoa
        dsc_nome_contato = customer.dsc_nome_contato
        dsc_cpf_cnpj = customer.dsc_cpf_cnpj
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
    
}