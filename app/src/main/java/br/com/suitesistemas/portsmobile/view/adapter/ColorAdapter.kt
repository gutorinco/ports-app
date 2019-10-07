package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Color
import br.com.suitesistemas.portsmobile.view.viewHolder.ColorViewHolder

class ColorAdapter(private val context: Context,
                   private val colors: MutableList<Color>,
                   private val delete: ((position: Int) -> Unit)? = null,
                   private val edit: ((position: Int) -> Unit)? = null
) : RecyclerView.Adapter<ColorViewHolder>(), CustomAdapter<Color> {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ColorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]
        holder.bindView(color)
        if (delete != null && edit != null) {
            holder.menu.setOnClickListener {
                val popupMenu = PopupMenu(context, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_basic_adapter_delete -> {
                            delete.invoke(position)
                            true
                        }
                        R.id.menu_basic_adapter_edit -> {
                            edit.invoke(position)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.inflate(R.menu.menu_basic_adapter)

                try {
                    val popupField = PopupMenu::class.java.getDeclaredField("mPopup")
                    popupField.isAccessible = true
                    val popup = popupField.get(popupMenu)
                    popup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(popup, true)
                } catch (ex: Exception) {
                    Log.e("Main", "Error showing menu icons", ex)
                } finally {
                    popupMenu.show()
                }
            }
        } else {
            holder.menu.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount() = colors.size

    override fun setAdapter(list: List<Color>) {
        colors.clear()
        colors.addAll(list)
        notifyDataSetChanged()
    }

}