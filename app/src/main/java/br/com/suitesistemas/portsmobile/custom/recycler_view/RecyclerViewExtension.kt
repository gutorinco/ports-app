package br.com.suitesistemas.portsmobile.custom.recycler_view

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.custom.button.hideToBottom
import br.com.suitesistemas.portsmobile.custom.button.showFromBottom
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

fun RecyclerView.hideButtonOnScroll(button: FloatingActionButton) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0 && button.visibility == View.VISIBLE) {
                button.hideToBottom()
            } else if (dy < 0 && button.visibility != View.VISIBLE) {
                button.showFromBottom()
            }
        }
    })
}