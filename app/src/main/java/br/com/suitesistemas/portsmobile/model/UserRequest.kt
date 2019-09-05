package br.com.suitesistemas.portsmobile.model

class UserRequest(
    val empresa: String,
    val usuario: String,
    val senha: String,
    val token: String = "") {}