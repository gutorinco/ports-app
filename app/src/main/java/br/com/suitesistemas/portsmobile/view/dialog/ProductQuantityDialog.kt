package br.com.suitesistemas.portsmobile.view.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.entity.ProductColor
import br.com.suitesistemas.portsmobile.model.ProductDetail
import br.com.suitesistemas.portsmobile.view.adapter.ProductQuantityAdapter
import kotlinx.android.synthetic.main.dialog_product_quantity.*

class ProductQuantityDialog : DialogFragment() {

    private val products: MutableList<Product> = mutableListOf()
    private val productsColors: MutableList<ProductColor> = mutableListOf()
    private val productsQuantities: LinkedHashMap<Product, ProductDetail> = linkedMapOf()
    private val response: LinkedHashMap<Product, ProductDetail> = linkedMapOf()
    private var confirmed = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context!!).inflate(R.layout.dialog_product_quantity, container)
    }

    override fun onResume() {
        super.onResume()
        initProducts()
        initColors()
        configureList()
        configureButton()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (confirmed) {
            confirmed = false
        } else {
            onDismiss()
        }
    }

    fun show(fragmentManager: FragmentManager) {
        if (fragmentManager.findFragmentByTag(PRODUCT_DIALOG_TAG) == null)
            show(fragmentManager, PRODUCT_DIALOG_TAG)
    }

    private fun initProducts() {
        arguments?.let {
            val productsList = it.getParcelableArrayList<Product>("products")
            productsList?.let {
                products -> this.products.addAll(products)
            }
        }
    }

    private fun initColors() {
        arguments?.let {
            val colorsList = it.getParcelableArrayList<ProductColor>("colors")
            colorsList?.let { colors ->
                productsColors.addAll(colors)
            }
            products.forEach { product ->
                val productColors: MutableList<ProductColor> = productsColors.filter {
                        color -> color.cod_produto.cod_produto == product.cod_produto
                } as MutableList<ProductColor>
                productsQuantities[product] = ProductDetail(1, productColors)
            }
        }
    }

    private fun configureList() {
        product_dialog_list.adapter = ProductQuantityAdapter(context!!, productsQuantities) {
            quantity, productColor, product ->
                if (response.keys.isNullOrEmpty()) {
                    response[product] = ProductDetail(quantity, mutableListOf(productColor))
                } else {
                    var existProduct = response.keys.find { it.cod_produto == product.cod_produto }
                    if (existProduct == null)
                        existProduct = product
                    response[existProduct] = ProductDetail(quantity, mutableListOf(productColor))
                }
            }
    }

    private fun configureButton() {
        product_dialog_btn_save.setOnClickListener {
            confirmed = true
            dismiss()
            (product_dialog_list.adapter as ProductQuantityAdapter).clearFocus()
            callback(response)
        }
    }

    companion object {
        private const val PRODUCT_DIALOG_TAG = "PRODUCT_DIALOG_TAG"
        private lateinit var callback: (productsQuantities: LinkedHashMap<Product, ProductDetail>) -> Unit
        private lateinit var onDismiss: () -> Unit

        fun newInstance(products: List<Product>,
                        productsColors: List<ProductColor>,
                        callback: (productsQuantities: LinkedHashMap<Product, ProductDetail>) -> Unit,
                        onDismiss: () -> Unit): ProductQuantityDialog {
            val bundle = Bundle()
            bundle.putParcelableArrayList("products", ArrayList(products))
            bundle.putParcelableArrayList("colors", ArrayList(productsColors))

            val dialog = ProductQuantityDialog()
            this.callback = callback
            this.onDismiss = onDismiss
            dialog.arguments = bundle

            return dialog
        }
    }

}