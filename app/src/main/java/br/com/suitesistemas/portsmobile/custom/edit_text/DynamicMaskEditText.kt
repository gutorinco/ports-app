package br.com.suitesistemas.portsmobile.custom.edit_text

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText

class DynamicMaskEditText : EditText {

    var listeners: MutableList<TextWatcher>? = null

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyle: Int) : super(ctx, attrs, defStyle)

    override fun addTextChangedListener(watcher: TextWatcher) {
        if (listeners == null) {
            listeners = mutableListOf()
        } else {
            with(listeners!!) {
                forEach { watcher -> removeTextChangedListener(watcher) }
                clear()
            }
        }

        if (super.getTag() == "CustomMask")
            listeners?.add(watcher)
        super.addTextChangedListener(watcher)
    }

}