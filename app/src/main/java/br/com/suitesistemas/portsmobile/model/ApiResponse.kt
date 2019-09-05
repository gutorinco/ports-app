package br.com.suitesistemas.portsmobile.model

import br.com.suitesistemas.portsmobile.model.enums.EHttpOperation

class ApiResponse<T> {

    var data: T? = null
    var messageError: String? = null
    var operation = EHttpOperation.GET

    constructor(data: T, operation: EHttpOperation = EHttpOperation.GET) {
        this.data = data
        this.operation = operation
    }
    constructor(messageError: String, operation: EHttpOperation = EHttpOperation.GET) {
        this.messageError = messageError
        this.operation = operation
    }

}