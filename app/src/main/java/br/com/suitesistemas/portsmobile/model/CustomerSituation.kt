package br.com.suitesistemas.portsmobile.model

import br.com.suitesistemas.portsmobile.model.enums.ECustomerSituation

class CustomerSituation(value: String) {

    val flag: ECustomerSituation = when (value) {
        "Análise" -> ECustomerSituation.N
        "Ativo" -> ECustomerSituation.A
        "Pendência" -> ECustomerSituation.P
        else -> ECustomerSituation.C
    }

}