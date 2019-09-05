package br.com.suitesistemas.portsmobile.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Product(var cod_produto: Int? = 0,
              var dbl_compra_prazo: Double? = 0.0,
              var dbl_compra_vista: Double? = 0.0,
              var dbl_venda_prazo: Double? = 0.0,
              var dbl_venda_vista: Double? = 0.0,
              var dsc_observacao: String? = "",
              var dsc_produto: String? = "",
              var dsc_referencia: String? = "",
              var fky_empresa: Company? = Company(),
              var fky_unidade_medida: UnitMeasure? = UnitMeasure(),
              var version: Int? = 0) : Parcelable {}