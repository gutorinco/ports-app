package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.utils.PopupMenuUtils

abstract class BaseAdapter<T, K : RecyclerView.ViewHolder>(
    protected val context: Context,
    protected val list: MutableList<T>
) : RecyclerView.Adapter<K>(), CustomAdapter<T> {

    fun createPopup(view: View, delete: () -> Unit, edit: () -> Unit) {
        PopupMenuUtils.createPopup(view, context, delete, edit)
    }

    fun createPopup(view: View, delete: () -> Unit) {
        PopupMenuUtils.createPopup(view, context, delete)
    }

    fun configureToShowPopupIcons(popupMenu: PopupMenu) {
        PopupMenuUtils.configureToShowPopupIcons(popupMenu)
    }

    fun getItemBy(position: Int) = list[position]

    override fun getItemCount() = list.size

    override fun setAdapter(list: List<T>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

}