package br.com.suitesistemas.portsmobile.model

import java.io.Serializable

class UserResponse : Serializable {

    var codigo: Int = 0
    var usuario: String = ""
    var empresa: String = ""
    var area: String = ""

}