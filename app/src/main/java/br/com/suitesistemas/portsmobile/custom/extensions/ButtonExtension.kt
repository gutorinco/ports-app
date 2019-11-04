package br.com.suitesistemas.portsmobile.custom.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import br.com.suitesistemas.portsmobile.R
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

@SuppressLint("RestrictedApi")
fun FloatingActionButton.showFromBottom() {
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.show_from_bottom)
    with (animation) {
        duration = 400
        interpolator = DecelerateInterpolator()
        repeatMode = Animation.REVERSE
    }
    startAnimation(animation)
}

@SuppressLint("RestrictedApi")
fun FloatingActionButton.hideToBottom() {
    visibility = View.GONE
    val animation = AnimationUtils.loadAnimation(context, R.anim.hide_to_bottom)
    with (animation) {
        duration = 400
        interpolator = AccelerateInterpolator()
        repeatMode = Animation.REVERSE
    }
    startAnimation(animation)
}

fun MaterialButton.showProgressSpinner(buttonText: Int) {
    isEnabled = false
    background.setColorFilter(
        getCustomColor(
            context,
            R.color.success_disabled
        ), android.graphics.PorterDuff.Mode.MULTIPLY)
    showProgress {
        buttonTextRes = buttonText
        progressColorRes = android.R.color.white
        gravity = com.github.razir.progressbutton.DrawableButton.GRAVITY_TEXT_END
    }
}

fun MaterialButton.hideProgressSpinner(buttonText: Int) {
    isEnabled = true
    background.setColorFilter(
        getCustomColor(
            context,
            R.color.success
        ), android.graphics.PorterDuff.Mode.MULTIPLY)
    hideProgress(buttonText)
}

private fun getCustomColor(context: Context, colorId: Int): Int {
    return ContextCompat.getColor(context, colorId)
}