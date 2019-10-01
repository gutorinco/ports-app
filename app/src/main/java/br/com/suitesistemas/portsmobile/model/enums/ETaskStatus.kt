package br.com.suitesistemas.portsmobile.model.enums

enum class ETaskStatus(newValue: Int) {

    A_INICIAR(1), INICIO_ATRASADO(2), EM_ANDAMENTO(3), ATRASADO(4), CONCLUIDO(5);

    var value: Int? = newValue

}