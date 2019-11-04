package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.CustomerViewHolder
import br.com.suitesistemas.portsmobile.view.dialog.CustomerPhonesDialog

class CustomerAdapter(context: Context,
     private val activity: FragmentActivity,
     customers: MutableList<Customer>,
     private val delete: (position: Int) -> Unit,
     private val edit: (position: Int) -> Unit,
     private val startActivity: (request: String, param: String) -> Unit,
     private val failure: (stringId: Int) -> Unit
) : BaseAdapter<Customer, CustomerViewHolder>(context, customers) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CustomerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = list[position]
        holder.bindView(customer)
        holder.menu.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_customer_adapter_delete -> {
                        delete(position)
                        true
                    }
                    R.id.menu_customer_adapter_edit -> {
                        edit(position)
                        true
                    }
                    R.id.menu_customer_adapter_phone -> {
                        if (customer.dsc_fone_01.isNullOrEmpty() && customer.dsc_celular_01.isNullOrEmpty()) {
                            failure(R.string.nenhum_telefone)
                        } else if (!customer.dsc_fone_01.isNullOrEmpty() && !customer.dsc_celular_01.isNullOrEmpty()) {
                            val phone1 = getCompletePhone(customer.dsc_ddd_01!!, customer.dsc_fone_01!!)
                            val phone2 = getCompletePhone(customer.dsc_ddd_celular_01!!, customer.dsc_celular_01!!)
                            CustomerPhonesDialog.newInstance(arrayListOf(phone1, phone2)) { phoneSelected ->
                                startActivity("call", phoneSelected)
                            }.show(activity.supportFragmentManager)
                        } else if (!customer.dsc_fone_01.isNullOrEmpty()) {
                            val phone = getCompletePhone(customer.dsc_ddd_01!!, customer.dsc_fone_01!!)
                            startActivity("call", phone)
                        } else if (!customer.dsc_celular_01.isNullOrEmpty()) {
                            val phone = getCompletePhone(customer.dsc_ddd_celular_01!!, customer.dsc_celular_01!!)
                            startActivity("call", phone)
                        }
                        true
                    }
                    R.id.menu_customer_adapter_email -> {
                        if (customer.dsc_email.isNullOrEmpty()) {
                            failure(R.string.nenhum_email)
                        } else {
                            startActivity("email", customer.dsc_email!!)
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu_customer_adapter)

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

    private fun getCompletePhone(ddd: String, phone: String): String {
        return ddd.replace("(", "").replace(")", "") + phone.replace("-", "").replace(" ", "")
    }

}