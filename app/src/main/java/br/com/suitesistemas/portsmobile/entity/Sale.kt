package br.com.suitesistemas.portsmobile.entity

import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.util.*

class Sale : Serializable {

    var num_codigo_online: String = ""
    var cod_venda: Int? = null
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var dat_emissao: Date = Date()
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    var hor_emissao: Date = Date()
    var dbl_total_produtos: Double = 0.0
    var dbl_total_venda: Double = 0.0
    var dbl_valor_pago: Double = 0.0
    var dsc_observacao: String? = ""
    var fky_empresa: Company = Company()
    var fky_cliente: Customer = Customer()
    var fky_vendedor: Customer = Customer()
    var fky_condicao_pagamento: PaymentConditions = PaymentConditions()
    var flg_cadastrado_app: EYesNo = EYesNo.S
    var flg_cadastrado_online: EYesNo = EYesNo.N
    var flg_integrado_online: EYesNo = EYesNo.N
    var version: Int = 0

    fun copy(sale: Sale) {
        num_codigo_online = sale.num_codigo_online
        cod_venda = sale.cod_venda
        dat_emissao = sale.dat_emissao
        hor_emissao = sale.hor_emissao
        dbl_total_produtos = sale.dbl_total_produtos
        dbl_total_venda = sale.dbl_total_venda
        dbl_valor_pago = sale.dbl_valor_pago
        dsc_observacao = sale.dsc_observacao
        fky_empresa = sale.fky_empresa
        fky_cliente = sale.fky_cliente
        fky_vendedor = sale.fky_vendedor
        fky_condicao_pagamento = sale.fky_condicao_pagamento
        flg_cadastrado_app = sale.flg_cadastrado_app
        flg_cadastrado_online = sale.flg_cadastrado_online
        flg_integrado_online = sale.flg_integrado_online
        version = sale.version
    }

}