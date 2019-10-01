package br.com.suitesistemas.portsmobile.entity

import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import java.io.Serializable

class Color(name: String? = "") : Serializable {

    var num_codigo_online: String = ""
    var cod_cor: Int? = null
    var dsc_cor: String? = null
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_integrado_online: EYesNo = EYesNo.N
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var version: Int = 0

    init {
        if (name != null)
            dsc_cor = name
    }

    fun copy(color: Color) {
        num_codigo_online = color.num_codigo_online
        cod_cor = color.cod_cor
        dsc_cor = color.dsc_cor
        flg_cadastrado_app = color.flg_cadastrado_app
        flg_integrado_online = color.flg_integrado_online
        flg_cadastrado_online = color.flg_cadastrado_online
        version = color.version
    }

}