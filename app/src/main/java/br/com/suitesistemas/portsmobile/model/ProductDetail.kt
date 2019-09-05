package br.com.suitesistemas.portsmobile.model

import br.com.suitesistemas.portsmobile.entity.ProductColor

class ProductDetail(var quantity: Int = 0,
                    var colors: MutableList<ProductColor> = mutableListOf()) {
}