package br.com.suitesistemas.portsmobile.custom.button

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import br.com.suitesistemas.portsmobile.R
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