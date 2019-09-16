package br.com.suitesistemas.portsmobile.view.adapter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Customer
import br.com.suitesistemas.portsmobile.view.dialog.CustomerPhonesDialog
import br.com.suitesistemas.portsmobile.view.viewHolder.CustomerViewHolder

class CustomerAdapter(private val context: Context,
                      private val activity: FragmentActivity,
                      private val customers: MutableList<Customer>,
                      private val startActivity: (intent: Intent) -> Unit,
                      private val failure: (stringId: Int) -> Unit,
                      private val delete: (position: Int) -> Unit,
                      private val edit: (position: Int) -> Unit) :
    Adapter<CustomerViewHolder>(), CustomAdapter<Customer> {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CustomerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_customer_adapter, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customers[position]
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
                                makeCall(phoneSelected)
                            }.show(activity.supportFragmentManager)
                        } else if (!customer.dsc_fone_01.isNullOrEmpty()) {
                            val phone = getCompletePhone(customer.dsc_ddd_01!!, customer.dsc_fone_01!!)
                            makeCall(phone)
                        } else if (!customer.dsc_celular_01.isNullOrEmpty()) {
                            val phone = getCompletePhone(customer.dsc_ddd_celular_01!!, customer.dsc_celular_01!!)
                            makeCall(phone)
                        }
                        true
                    }
                    R.id.menu_customer_adapter_email -> {
                        if (customer.dsc_email.isNullOrEmpty()) {
                            failure(R.string.nenhum_email)
                        } else {
                            sendEmail(customer.dsc_email!!)
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

    override fun getItemCount() = customers.size

    override fun setAdapter(list: List<Customer>) {
        customers.clear()
        customers.addAll(list)
        notifyDataSetChanged()
    }

    private fun getCompletePhone(ddd: String, phone: String): String {
        return ddd.replace("(", "").replace(")", "") + phone.replace("-", "").replace(" ", "")
    }

    private fun makeCall(phone: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 0)
        } else {
            dial(phone)
        }
    }

    private fun dial(phone: String) {
        val uri = Uri.parse("tel:$phone")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        if (intent.resolveActivity(activity.packageManager!!) != null)
            startActivity(intent)
        else
            failure(R.string.acao_nao_suportada)
    }

    private fun sendEmail(email: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 0)
        } else {
            val uri = Uri.parse("mailto:$email")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            if (intent.resolveActivity(activity.packageManager!!) != null)
                startActivity(Intent.createChooser(intent, "Enviar email..."))
            else
                failure(R.string.acao_nao_suportada)
        }
    }

}