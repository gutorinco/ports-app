package br.com.suitesistemas.portsmobile.entity

import br.com.suitesistemas.portsmobile.model.enums.EFinancialReleaseTypeOperation
import br.com.suitesistemas.portsmobile.model.key.FinancialReleaseKey
import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.util.*

class FinancialRelease : Serializable {

    var ids: FinancialReleaseKey = FinancialReleaseKey()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_emissao: Date = Date()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_vencimento: Date = Date()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_pagamento: Date? = Date()
    var dbl_valor_total: Double = 0.0
    var dsc_observacao: String? = null
    var dsc_referencia: String = ""
    var flg_tipo_operacao: EFinancialReleaseTypeOperation = EFinancialReleaseTypeOperation.P
    var fky_empresa: Company = Company()
    var fky_pessoa: Customer = Customer()
    var fky_vendedor: Customer? = Customer()
    var fky_tipo_documento: DocumentType = DocumentType()
    var fky_situacao_pagamento: PaymentSituation = PaymentSituation()
    var version: Int = 0

}