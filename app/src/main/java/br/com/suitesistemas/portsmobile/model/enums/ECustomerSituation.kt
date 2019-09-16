package br.com.suitesistemas.portsmobile.model.enums

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = EnumDeserializer::class)
enum class ECustomerSituation {
    N, A, P, C; //Análise, Ativo, Pendência, Cancelado
}