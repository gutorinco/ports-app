package br.com.suitesistemas.portsmobile.entity

import java.io.Serializable

class PaymentSituation(var cod_situacao_pagamento: Int? = 0,
                       var dsc_situacao_pagamento: String? = "",
                       var version: Int? = 0): Serializable {
}