package br.com.suitesistemas.portsmobile.entity

import java.io.Serializable

class DocumentType(var cod_tipo_documento: Int? = 0,
                   var dsc_tipo_documento: String? = "",
                   var version: Int? = 0): Serializable {
}