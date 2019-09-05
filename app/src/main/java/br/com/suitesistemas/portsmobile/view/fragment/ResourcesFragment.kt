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
import br.com.suitesistemas.portsmobile.model.MainResources
import br.com.suitesistemas.portsmobile.view.adapter.MainResourcesAdapter
import kotlinx.android.synthetic.main.fragment_resources.*

class ResourcesFragment : Fragment() {

    private lateinit var delegate: Delegate

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_resources, container, false)
    }

    override fun onAttach(context: Context?) {
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
        val resources = getResourceList()
        with (list_resources) {
            adapter = MainResourcesAdapter(context, resources)
            setOnItemClickListener { _, _, position, _ -> delegate.onResourceSelected(getFragmentBy(position)) }
        }
    }

    private fun getResourceList(): List<MainResources> {
        val resources: MutableList<MainResources> = mutableListOf()
        with (resources) {
            add(
                MainResources(
                    getDrawableBy(R.drawable.ic_people_accent),
                    getString(R.string.clientes),
                    getString(R.string.gerencie_clientes)
                )
            )
            add(
                MainResources(
                    getDrawableBy(R.drawable.ic_cart_accent),
                    getString(R.string.vendas),
                    getString(R.string.gerencie_vendas)
                )
            )
//            add(
//                MainResources(
//                    getDrawableBy(R.drawable.ic_assigment),
//                    "Pedidos",
//                    "Cadastre novos pedidos"
//                )
//            )
            add(
                MainResources(
                    getDrawableBy(R.drawable.ic_credit_card_accent),
                    getString(R.string.lancamentos),
                    getString(R.string.gerencie_lancamentos)
                )
            )
//            add(
//                MainResources(
//                    getDrawableBy(R.drawable.ic_calendar_text),
//                    "Tarefas",
//                    "Controle de tarefas"
//                )
//            )
//            add(
//                MainResources(
//                    getDrawableBy(R.drawable.ic_handshake),
//                    "CRM",
//                    "Gestão de relacionamentos com o cliente"
//                )
//            )
        }
        return resources
    }

    private fun getFragmentBy(position: Int): Fragment {
        return when (position) {
            0 -> CustomerFragment()
            1 -> SaleFragment()
            2 -> FinancialFragment()
            3 -> OrderFragment()
            4 -> TaskFragment()
            5 -> CRMFragment()
            else -> CustomerFragment()
        }
    }

    private fun getDrawableBy(drawableResource: Int): Drawable? {
        return ContextCompat.getDrawable(context!!, drawableResource)
    }

    interface Delegate {
        fun onResourceSelected(fragment: Fragment)
    }
}
