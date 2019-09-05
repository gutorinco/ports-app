package br.com.suitesistemas.portsmobile.entity

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductColor(var cod_produto: Product = Product(),
                   var cod_cor: Color = Color()
): Parcelable {}