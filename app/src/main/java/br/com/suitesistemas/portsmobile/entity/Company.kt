package br.com.suitesistemas.portsmobile.entity

import java.io.Serializable

class Company : Serializable {

    var cod_empresa: Int = 0
    var dsc_empresa: String = ""
    var dsc_apelido: String = ""
    var fky_pessoa: Int = 0
    val version: Int = 0

}