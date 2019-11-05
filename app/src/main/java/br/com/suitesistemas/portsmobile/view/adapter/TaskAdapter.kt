package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.model.entity.Task
import br.com.suitesistemas.portsmobile.view.adapter.viewHolder.TaskViewHolder

class TaskAdapter(context: Context,
                  tasks: MutableList<Task>,
                  private val delete: (position: Int) -> Unit,
                  private val edit: (position: Int) -> Unit) : BaseAdapter<Task, TaskViewHolder>(context, tasks) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = list[position]
        holder.bindView(task)
        holder.menu.setOnClickListener {
            super.createPopup(it, {
                delete.invoke(position)
            }, {
                edit.invoke(position)
            })
        }
    }

}