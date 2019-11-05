package br.com.suitesistemas.portsmobile.model.entity

interface ChangeableModel<T> {
    fun copy(obj: T)
    fun getId() : String
    override fun equals(other: Any?): Boolean
}