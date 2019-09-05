package br.com.suitesistemas.portsmobile.custom.recycler_view

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {
    fun onItemClicked(position: Int, view: View)
}

fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {

    addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View) {
            view.setOnClickListener(null)
        }

        override fun onChildViewAttachedToWindow(view: View) {
            view.setOnClickListener {
                val holder = getChildViewHolder(view)
                onClickListener.onItemClicked(holder.adapterPosition, view)
            }
        }
    })

}

fun RecyclerView.addSwipe(swipeToDelete: SwipeToDeleteCallback) {
    val itemTouchHelper = ItemTouchHelper(swipeToDelete)
    itemTouchHelper.attachToRecyclerView(this)
}