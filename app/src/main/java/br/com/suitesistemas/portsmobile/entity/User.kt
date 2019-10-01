package br.com.suitesistemas.portsmobile.entity

import java.io.Serializable

class User : Serializable {

    var cod_usuario: Int = 0
    var dsc_usuario: String? = ""
    var dsc_senha: String? = ""
    var dsc_token: String? = ""
    var fky_empresa: Company = Company()
    var fky_pessoa: Customer = Customer()
    var flg_tipo: String? = ""
    var flg_cadastrado_app: String? = ""
    var flg_cadastrado_online: String? = ""
    var flg_integrado_online: String? = ""
    var version: Int = 0

}