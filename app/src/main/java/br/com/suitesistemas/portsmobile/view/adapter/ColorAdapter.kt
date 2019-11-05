package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.Color
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.ColorViewHolder

class ColorAdapter(context: Context, colors: MutableList<Color>) : BaseAdapter<Color, ColorViewHolder>(context, colors) {

    private var delete: ((Int) -> Unit)? = null
    private var edit: ((Int) -> Unit)? = null

    constructor(context: Context,
                colors: MutableList<Color>,
                delete: (position: Int) -> Unit) : this(context, colors) {
        this.delete = delete
    }

    constructor(context: Context,
                colors: MutableList<Color>,
                delete: (position: Int) -> Unit,
                edit: (position: Int) -> Unit) : this(context, colors) {
        this.delete = delete
        this.edit = edit
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ColorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = list[position]
        holder.bindView(color)
        if (delete != null && edit != null) {
            holder.menu.setOnClickListener {
                super.createPopup(it, {
                    delete!!.invoke(position)
                }, {
                    edit!!.invoke(position)
                })
            }
        } else if (delete != null) {
            holder.menu.setOnClickListener {
                super.createPopup(it) {
                    delete!!.invoke(position)
                }
            }
        } else {
            holder.menu.visibility = View.INVISIBLE
        }
    }

}