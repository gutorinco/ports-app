package br.com.suitesistemas.portsmobile.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import br.com.suitesistemas.portsmobile.R

class PopupMenuUtils {

    companion object {

        fun createPopup(view: View, context: Context, delete: () -> Unit, edit: () -> Unit) {
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
            configureToShowPopupIcons(popupMenu)
        }

        fun createPopup(view: View, context: Context, delete: () -> Unit) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_basic_adapter_delete -> {
                        delete()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu_delete_adapter)
            configureToShowPopupIcons(popupMenu)
        }

        fun configureToShowPopupIcons(popupMenu: PopupMenu) {
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

    }
}