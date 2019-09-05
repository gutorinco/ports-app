package br.com.suitesistemas.portsmobile.entity

import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import java.io.Serializable

class SaleItem : Serializable {

    var num_codigo_online: String = ""
    var cod_sequencia: Int = 0
    var cod_venda: Int? = 0
    var dbl_preco_unit: Double = 0.0
    var dbl_quantidade: Double = 0.0
    var dbl_total_item: Double = 0.0
    var dsc_observacao: String? = null
    var fky_produto: Product = Product()
    var fky_cor: Color = Color()
    var fky_unidade_medida: UnitMeasure = UnitMeasure()
    var fky_empresa: Company = Company()
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int? = null

}