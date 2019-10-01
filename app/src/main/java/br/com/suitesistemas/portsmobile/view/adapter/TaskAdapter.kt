package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.entity.Task
import br.com.suitesistemas.portsmobile.view.viewHolder.TaskViewHolder

class TaskAdapter(private val context: Context,
                  private val tasks: MutableList<Task>,
                  private val delete: (position: Int) -> Unit,
                  private val edit: (position: Int) -> Unit) : RecyclerView.Adapter<TaskViewHolder>(), CustomAdapter<Task> {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_task_adapter, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bindView(task)
        holder.menu.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_basic_adapter_delete -> {
                        delete(position)
                        true
                    }
                    R.id.menu_basic_adapter_edit -> {
                        edit(position)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu_basic_adapter)

            try {
                val popupField = PopupMenu::class.java.getDeclaredField("mPopup")
                popupField.isAccessible = true
                val popup = popupField.get(popupMenu)
                popup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(popup, true)
            } catch (ex: Exception) {
                Log.e("Main", "Error showing menu icons", ex)
            } finally {
                popupMenu.show()
            }
        }
    }

    override fun getItemCount() = tasks.size

    override fun setAdapter(tasks: List<Task>) {
        this.tasks.clear()
        this.tasks.addAll(tasks)
        notifyDataSetChanged()
    }

}