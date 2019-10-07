package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Sale
import br.com.suitesistemas.portsmobile.view.viewHolder.SaleViewHolder

class SaleAdapter(private val context: Context,
                  private val sales: MutableList<Sale>,
                  private val delete: (position: Int) -> Unit,
                  private val edit: (position: Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SaleViewHolder>(),
    CustomAdapter<Sale> {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SaleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = sales[position]
        holder.bindView(sale)
        holder.menu.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_basic_adapter_delete -> {
                        delete(position)
                        true
                    }
                    R.id.menu_basic_adapter_edit -> {
                        edit(position)
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
    }

    override fun getItemCount() = sales.size

    override fun setAdapter(list: List<Sale>) {
        sales.clear()
        sales.addAll(list)
        notifyDataSetChanged()
    }

}