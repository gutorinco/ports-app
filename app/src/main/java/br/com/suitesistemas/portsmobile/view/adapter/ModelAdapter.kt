package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.Model
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.ModelViewHolder

class ModelAdapter(context: Context,
                   models: MutableList<Model>,
                   private val delete: (position: Int) -> Unit,
                   private val edit: (position: Int) -> Unit
) : BaseAdapter<Model, ModelViewHolder>(context, models) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_model, parent, false)
        return ModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = list[position]
        holder.bindView(model)
        holder.menu.setOnClickListener {
            super.createPopup(it, {
                delete(position)
            }, {
                edit(position)
            })
        }
    }

}