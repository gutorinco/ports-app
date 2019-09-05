package br.com.suitesistemas.portsmobile.custom.recycler_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import br.com.suitesistemas.portsmobile.R

class SwipeToDeleteCallback(adapterContext: Context, callback: (position: Int) -> Unit) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private lateinit var itemView: View
    private lateinit var canvas: Canvas
    private var deleteCallback: (position: Int) -> Unit = callback
    private var deleteIcon = ContextCompat.getDrawable(adapterContext, R.drawable.ic_trash)!!
    private val swipeBackground = ColorDrawable(Color.parseColor("#f0c808"))

    override fun onMove(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder, p2: androidx.recyclerview.widget.RecyclerView.ViewHolder) = false

    override fun onSwiped(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
        deleteCallback(holder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: androidx.recyclerview.widget.RecyclerView,
        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        itemView = viewHolder.itemView
        canvas = c

        drawSwipeBackground(dX)
        configureCanvas(dX)
        drawDeleteIcon(dX)

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawSwipeBackground(dX: Float) {
        setSwipeBackgroundBound(dX)
        swipeBackground.draw(canvas)
    }

    private fun drawDeleteIcon(dX: Float) {
        setDeleteIconBounds(dX)
        deleteIcon.draw(canvas)
    }

    private fun configureCanvas(dX: Float) {
        canvas.save()

        if (dX > 0)
            canvas.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
        else
            canvas.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

        canvas.restore()
    }

    private fun setSwipeBackgroundBound(dX: Float) {
        val swipeToTheRight = dX > 0

        if (swipeToTheRight) {
            swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
        } else {
            swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }
    }

    private fun setDeleteIconBounds(dX: Float) {
        val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
        var iconBottom: Int
        var iconLeft: Int
        var iconRight: Int
        var iconTop: Int

        if (dX > 0) {
            iconLeft = itemView.left + iconMargin
            iconTop = itemView.top + iconMargin
            iconRight = itemView.left + iconMargin + deleteIcon.intrinsicWidth
            iconBottom = itemView.bottom - iconMargin
        } else {
            iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
            iconTop = itemView.top + iconMargin
            iconRight = itemView.right - iconMargin
            iconBottom = itemView.bottom - iconMargin
        }

        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
    }

}