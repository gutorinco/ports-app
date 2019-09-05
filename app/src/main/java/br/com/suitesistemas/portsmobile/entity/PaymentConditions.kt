package br.com.suitesistemas.portsmobile.entity

import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import java.io.Serializable

class PaymentConditions : Serializable {

    var cod_condicao_pagamento: Int = 0
    var dsc_condicao_pagamento: String = ""
    var flg_a_vista = EYesNo.N
    var version: Int = 0

}