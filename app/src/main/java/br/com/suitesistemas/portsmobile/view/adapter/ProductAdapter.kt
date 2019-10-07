package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Product
import br.com.suitesistemas.portsmobile.view.viewHolder.ProductViewHolder

class ProductAdapter(private val context: Context,
                     private val products: MutableList<Product>,
                     private val delete: (position: Int) -> Unit,
                     private val edit: (position: Int) -> Unit
) : RecyclerView.Adapter<ProductViewHolder>(), CustomAdapter<Product> {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bindView(product)
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

    override fun getItemCount() = products.size

    override fun setAdapter(list: List<Product>) {
        products.clear()
        products.addAll(list)
        notifyDataSetChanged()
    }

}