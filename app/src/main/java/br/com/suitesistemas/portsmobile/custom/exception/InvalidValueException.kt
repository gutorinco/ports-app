package br.com.suitesistemas.portsmobile.custom.exception

class InvalidValueException : RuntimeException {

    var field: String? = null

    constructor(message: String) : super(message)
    constructor(field: String, message: String) : super(message) {
        this.field = field
    }

}