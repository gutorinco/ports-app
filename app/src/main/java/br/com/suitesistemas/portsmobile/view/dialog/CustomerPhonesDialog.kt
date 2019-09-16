package br.com.suitesistemas.portsmobile.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import br.com.suitesistemas.portsmobile.R
import kotlinx.android.synthetic.main.dialog_customer_phones.*

class CustomerPhonesDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context!!).inflate(R.layout.dialog_customer_phones, container)
    }

    override fun onResume() {
        super.onResume()
        arguments?.let {
            val phonesList = it.getCharSequenceArrayList("phones")
            phonesList?.let { phones ->
                customer_phone_dialog_phone_1.text = phones[0]
                customer_phone_dialog_phone_2.text = phones[1]
                customer_phone_dialog_1.setOnClickListener { sendPhoneSelected(phones[0].toString()) }
                customer_phone_dialog_2.setOnClickListener { sendPhoneSelected(phones[1].toString()) }
            }
        }
    }

    fun show(fragmentManager: FragmentManager) {
        if (fragmentManager.findFragmentByTag(CUSTOMER_PHONES_DIALOG_TAG) == null)
            show(fragmentManager, CUSTOMER_PHONES_DIALOG_TAG)
    }

    private fun sendPhoneSelected(phone: String) {
        callback(phone)
        dismiss()
    }

    companion object {
        private const val CUSTOMER_PHONES_DIALOG_TAG = "CUSTOMER_PHONES_DIALOG_TAG"
        private lateinit var callback: (phoneSelected: String) -> Unit

        fun newInstance(phones: List<String>, callback: (phoneSelected: String) -> Unit): CustomerPhonesDialog {
            val bundle = Bundle()
            bundle.putCharSequenceArrayList("phones", ArrayList(phones))

            val dialog = CustomerPhonesDialog()
            this.callback = callback
            dialog.arguments = bundle

            return dialog
        }
    }

}