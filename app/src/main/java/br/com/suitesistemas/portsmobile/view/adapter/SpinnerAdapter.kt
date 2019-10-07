package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.view.viewHolder.SpinnerViewHolder

class SpinnerAdapter(context: Context, private var items: List<String>) : ArrayAdapter<String>(context, 0, items) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        lateinit var holder: SpinnerViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_spinner_array, parent, false)
            holder = SpinnerViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as SpinnerViewHolder
        }

        if (!items.isNullOrEmpty())
            holder.bindView(items[position])

        return view!!
    }

}