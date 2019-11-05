package br.com.suitesistemas.portsmobile.view.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.custom.extensions.actionDoneClicked
import br.com.suitesistemas.portsmobile.custom.extensions.hideProgressSpinner
import br.com.suitesistemas.portsmobile.custom.extensions.showProgressSpinner
import br.com.suitesistemas.portsmobile.model.entity.Color
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import kotlinx.android.synthetic.main.dialog_color_form.*

class ColorFormDialog : DialogFragment(), View.OnClickListener {

    private var color = Color()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context!!).inflate(R.layout.dialog_color_form, container)
    }

    override fun onResume() {
        super.onResume()
        arguments?.let {
            color = it.getParcelable("color") as Color
            dialog_color_form_name.setText(color.dsc_cor)
        }
        dialog_color_form_name.actionDoneClicked { onClick(null) }
        configureButton()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialog_color_form_button.hideProgressSpinner(R.string.salvar)
    }

    private fun configureButton() {
        bindProgressButton(dialog_color_form_button)
        with (dialog_color_form_button) {
            attachTextChangeAnimator {
                fadeInMills = 50
                fadeOutMills = 50
            }
            setOnClickListener(this@ColorFormDialog)
        }
    }

    override fun onClick(v: View?) {
        dialog_color_form_button.showProgressSpinner(R.string.salvando)
        val colorName = dialog_color_form_name.text.toString()
        color.dsc_cor = colorName
        callback(color)
    }

    fun show(fragmentManager: FragmentManager) {
        if (fragmentManager.findFragmentByTag(COLOR_FORM_DIALOG_TAG) == null)
            show(fragmentManager, COLOR_FORM_DIALOG_TAG)
    }

    companion object {

        private const val COLOR_FORM_DIALOG_TAG = "COLOR_FORM_DIALOG_TAG"
        private lateinit var callback: (color: Color) -> Unit

        fun newInstance(color: Color?, callback: (color: Color) -> Unit): ColorFormDialog {
            val bundle = Bundle()
            bundle.putParcelable("color", color)

            val dialog = ColorFormDialog()
            this.callback = callback
            dialog.arguments = bundle

            return dialog
        }
    }

}