package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.view.viewHolder.SelectColorViewHolder

class SelectColorAdapter(private val context: Context,
                         private val colors: MutableList<Color>,
                         private val onChecked: (checked: Boolean, position: Int) -> Unit
) : RecyclerView.Adapter<SelectColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SelectColorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_select_color, parent, false)
        return SelectColorViewHolder(view)
    }

    override fun onBindViewHolder(holderSelect: SelectColorViewHolder, position: Int) {
        val color = colors[position]
        holderSelect.bindView(color)
        holderSelect.checkbox.setOnClickListener {
            it?.let { view ->
                val checkbox = view as CheckBox
                onChecked(checkbox.isChecked, holderSelect.adapterPosition)
            }
        }
    }

    override fun getItemCount() = colors.size

    fun setAdapter(toAdd: List<Color>) {
        colors.clear()
        colors.addAll(toAdd)
        notifyDataSetChanged()
    }

}