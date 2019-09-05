package br.com.suitesistemas.portsmobile.model.key

import br.com.suitesistemas.portsmobile.model.enums.EFinancialReleaseStatus
import java.io.Serializable

class FinancialReleaseKey : Serializable {

    var cod_lancamento_financeiro: Int = 0
    var cod_serie: Int = 0
    var cod_sequencia: Int = 0
    var fky_tipo_documento: Int = 0
    var flg_status: EFinancialReleaseStatus = EFinancialReleaseStatus.A

}