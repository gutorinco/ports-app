package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.MainResources
import kotlinx.android.synthetic.main.fragment_resources_adapter.view.*

class MainResourcesAdapter(private val context: Context,
                           private val resources: List<MainResources>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val resource = getItem(position)
        val inflate = LayoutInflater.from(context).inflate(R.layout.fragment_resources_adapter, parent, false)

        with (inflate) {
            main_resources_img.setImageDrawable(resource.drawable)
            main_resources_title.text = resource.title
            main_resources_description.text = resource.description
        }

        return inflate
    }

    override fun getItem(position: Int) = resources[position]

    override fun getItemId(position: Int) = Long.MIN_VALUE

    override fun getCount() = resources.size

}