package br.com.suitesistemas.portsmobile.custom.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.utils.DoubleUtils

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            setTag(R.id.textWatcher, this)
            afterTextChanged.invoke(s.toString())
        }
    })
}

fun EditText.actionDoneClicked(clicked: (View?) -> Unit) {
    this.setOnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE)
            clicked.invoke(v)
        false
    }
}

fun EditText.setTextSafely(value: Double) {
    setTextSafely(value.toStringValue())
}

fun EditText.setTextSafely(value: Int) {
    setTextSafely(value.toString())
}

fun EditText.setTextSafely(text: String) {
    val watcher = getTag(R.id.textWatcher)
    if (watcher == null) {
        setText(text)
    } else {
        removeTextChangedListener(watcher as TextWatcher)
        setText(text)
        addTextChangedListener(watcher)
    }
}

fun EditText.addNumberMask() {
    this.tag = "NumberMask"
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) {
            val typedText = value.toString().removeLetters()
            val backspaceWasPressed = count <= before

            val newValue = if (typedText.isNotEmpty()) {
                val last = typedText.last().toString()
                if ((last == "." || last == ",") && backspaceWasPressed) {
                    typedText
                } else if ((last == "." || last == ",") && !backspaceWasPressed) {
                    typedText.replaceRange(typedText.length - 1, typedText.length, ",")
                } else {
                    val value = typedText.replace(".", "").replace(",", ".")
                    DoubleUtils().getFormattedValue(value)
                }
            } else {
                ""
            }

            removeTextChangedListener(this)
            setText(newValue)
            setSelection(text.length)
            addTextChangedListener(this)
        }
    }
    this.addTextChangedListener(textWatcher)
    this.setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            val editText = (v as EditText)
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                val value = DoubleUtils().formatParts(text)
                with (editText) {
                    removeTextChangedListener(textWatcher)
                    setText(value)
                    addTextChangedListener(textWatcher)
                }
            }
        }
    }
}

fun EditText.toDoubleValue() : Double {
    var value = 0.0
    val valueText = text.toString()
    if (valueText.isNotEmpty()) {
        val doubleValue = valueText.replace(".", "").replace(",", ".").toDouble()
        val formattedValue = DoubleUtils().toStringFormat(doubleValue)
        value = formattedValue.replace(".", "").replace(",", ".").toDouble()
    }
    return value
}

fun EditText.addCpfMask() {
    this.tag = "CustomMask"
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) {
            var text = value.toString().removeWhitespaces().replace(".", "").replace("-", "")
            val backspaceWasPressed = count <= before
            var newValue = ""

            if (text.length > 11)
                text = text.substring(0, 11)

            if (text.isNotEmpty()) {
                for (index in text.indices) {
                    if (index == 2 && !(backspaceWasPressed && text.length == 3))
                        newValue = newValue + text[index] + "."
                    else if (index == 5 && !(backspaceWasPressed && text.length == 6))
                        newValue = newValue + text[index] + "."
                    else if (index == 8 && !(backspaceWasPressed && text.length == 9))
                        newValue = newValue + text[index] + "-"
                    else
                        newValue += text[index]
                }
            } else {
                newValue = text
            }

            removeTextChangedListener(this)
            setText(newValue)
            setSelection(this@addCpfMask.text.length)
            addTextChangedListener(this)
        }
    })
}

fun EditText.addCnpjMask() {
    this.tag = "CustomMask"
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) {
            var text = value.toString().numbersOnly()
            val backspaceWasPressed = count <= before
            var newValue = ""

            if (text.length > 14)
                text = text.substring(0, 14)

            if (text.isNotEmpty()) {
                for (index in text.indices) {
                    if (index == 1 && !(backspaceWasPressed && text.length == 2))
                        newValue = newValue + text[index] + "."
                    else if (index == 4 && !(backspaceWasPressed && text.length == 5))
                        newValue = newValue + text[index] + "."
                    else if (index == 7 && !(backspaceWasPressed && text.length == 8))
                        newValue = newValue + text[index] + "/"
                    else if (index == 11 && !(backspaceWasPressed && text.length == 12))
                        newValue = newValue + text[index] + "-"
                    else
                        newValue += text[index]
                }
            } else {
                newValue = text
            }

            removeTextChangedListener(this)
            setText(newValue)
            setSelection(this@addCnpjMask.text.toString().length)
            addTextChangedListener(this)
        }
    })
}

fun EditText.addPhoneMask() {
    this.tag = "CustomMask"
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) {
            var text = value.toString().removeWhitespaces().replace("-", "").replace("(", "").replace(")", "")
            val backspaceWasPressed = count <= before

            if (text.length > 11)
                text = text.substring(0, 11)

            var newValue = ""
            if (backspaceWasPressed) {
                newValue = when {
                    text.length == 1 -> getPhoneValue(
                        text,
                        newValue,
                        1
                    )
                    text.length == 2 -> getPhoneValue(
                        text,
                        newValue,
                        1,
                        1
                    )
                    text.length <= 6 -> getPhoneValue(
                        text,
                        newValue,
                        5,
                        5
                    )
                    text.length > 6 -> getPhoneValue(
                        text,
                        newValue,
                        5
                    )
                    else -> text
                }
            } else {
                newValue = when {
                    text.length == 11 -> getPhoneValue(
                        text,
                        newValue,
                        6
                    )
                    text.length < 6 -> getPhoneValue(
                        text,
                        newValue,
                        5
                    )
                    text.length >= 6 -> getPhoneValue(
                        text,
                        newValue,
                        5
                    )
                    else -> text
                }
            }

            removeTextChangedListener(this)
            setText(newValue)
            setSelection(newValue.length)
            addTextChangedListener(this)
        }
    })
}

private fun getPhoneValue(text: String, newValue: String, dashPosition: Int) : String {
    var value = newValue
    for (index in text.indices) {
        when (index) {
            0 -> value = "(" + text[index]
            1 -> value = value + text[index] + ") "
            dashPosition -> value = value + text[index] + "-"
            else -> value += text[index]
        }
    }
    return value
}

private fun getPhoneValue(text: String, newValue: String, dashPosition: Int, skipPositon: Int) : String {
    var value = newValue
    for (index in text.indices) {
        if (index == skipPositon && skipPositon != 5) {
            value += text[index]
        } else if (index == 0) {
            value = "(" + text[index]
        } else if (index == 1) {
            value = value + text[index] + ") "
        } else if (index == dashPosition && dashPosition != skipPositon) {
            value = value + text[index] + "-"
        } else {
            value += text[index]
        }
    }
    return value
}