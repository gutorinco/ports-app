package br.com.suitesistemas.portsmobile.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.content.ContextCompat
import info.androidhive.fontawesome.FontDrawable

class IconUtils {

    companion object {

        fun get(context: Context, iconId: Int, colorId: Int, size: Float = 24F): FontDrawable {
            val boxesIcon = FontDrawable(context, iconId, true, false)
            val color = ContextCompat.getColor(context, colorId)
            val porterDuffColorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            boxesIcon.colorFilter = porterDuffColorFilter
            boxesIcon.textSize = size
            return boxesIcon
        }

    }
}