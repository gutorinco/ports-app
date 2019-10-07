package br.com.suitesistemas.portsmobile.view.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.utils.IconUtils
import kotlinx.android.synthetic.main.adapter_resources.view.*
import kotlinx.android.synthetic.main.fragment_resources.*

class ResourcesFragment : Fragment() {

    private lateinit var delegate: Delegate

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_resources, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Delegate)
            delegate = context
    }

    override fun onResume() {
        super.onResume()
        configureTitle()
        configureList()
    }

    private fun configureTitle() {
        activity?.setTitle(R.string.app_name)
    }

    private fun configureList() {
        with (fragment_res_people) {
            main_resources_img.setImageDrawable(getDrawableBy(R.drawable.ic_people_accent))
            main_resources_title.text = getString(R.string.clientes)
            setOnClickListener { delegate.onResourceSelected(0) }
        }
        with (fragment_res_sale) {
            main_resources_img.setImageDrawable(getDrawableBy(R.drawable.ic_cart_accent))
            main_resources_title.text = getString(R.string.vendas)
            setOnClickListener { delegate.onResourceSelected(1) }
        }
        with (fragment_res_product) {
            val boxesIcon = IconUtils.get(context!!, R.string.fa_boxes_solid, R.color.icons_accent, 32F)
            main_resources_img.setImageDrawable(boxesIcon)
            main_resources_title.text = getString(R.string.produtos)
            setOnClickListener { delegate.onResourceSelected(2) }
        }
        with (fragment_res_color) {
            main_resources_img.setImageDrawable(getDrawableBy(R.drawable.ic_color_accent))
            main_resources_title.text = getString(R.string.cores)
            setOnClickListener { delegate.onResourceSelected(3) }
        }
        with (fragment_res_model) {
            main_resources_img.setImageDrawable(getDrawableBy(R.drawable.ic_model_accent))
            main_resources_title.text = getString(R.string.modelos)
            setOnClickListener { delegate.onResourceSelected(4) }
        }
        with (fragment_res_financial_release) {
            main_resources_img.setImageDrawable(getDrawableBy(R.drawable.ic_credit_card_accent))
            main_resources_title.text = getString(R.string.lancamentos)
            setOnClickListener { delegate.onResourceSelected(5) }
        }
    }

    private fun getDrawableBy(drawableResource: Int): Drawable? {
        return ContextCompat.getDrawable(context!!, drawableResource)
    }

    interface Delegate {
        fun onResourceSelected(itemPosition: Int)
    }
}
