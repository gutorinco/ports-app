package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R

abstract class BaseAdapter<T, K : RecyclerView.ViewHolder>(
    protected val context: Context,
    protected val list: MutableList<T>
) : RecyclerView.Adapter<K>(), CustomAdapter<T> {

    fun createPopup(view: View, delete: () -> Unit, edit: () -> Unit) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_basic_adapter_delete -> {
                    delete()
                    true
                }
                R.id.menu_basic_adapter_edit -> {
                    edit()
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

    override fun getItemCount() = list.size

    override fun setAdapter(list: List<T>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

}