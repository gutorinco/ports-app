package br.com.suitesistemas.portsmobile.view.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.suitesistemas.portsmobile.custom.extensions.toStringFormat
import br.com.suitesistemas.portsmobile.entity.Task
import kotlinx.android.synthetic.main.adapter_task.view.*
import java.util.*

class TaskViewHolder(adapterView: View) : RecyclerView.ViewHolder(adapterView) {

    val description = adapterView.task_adapter_description
    val start = adapterView.task_adapter_start
    val end = adapterView.task_adapter_end
    val startPrev = adapterView.task_adapter_start_prev
    val endPrev = adapterView.task_adapter_end_prev
    val dateLayout = adapterView.task_adapter_date_layout
    val prevLayout = adapterView.task_adapter_prev_layout
    val menu = adapterView.task_adapter_menu

    fun bindView(task: Task) {
        description.text  = task.dsc_tarefa
        if (task.dat_inicio == null && task.dat_termino == null) {
            dateLayout.visibility = View.GONE
        } else {
            start.text = getDateString(task.dat_inicio)
            end.text = getDateString(task.dat_termino)
        }

        if (task.dat_prev_inicio == null && task.dat_prev_termino == null) {
            prevLayout.visibility = View.GONE
        } else {
            startPrev.text = getDateString(task.dat_prev_inicio)
            endPrev.text = getDateString(task.dat_prev_termino)
        }
    }

    private fun getDateString(date: Date?): String {
        return date?.toStringFormat() ?: "N/A"
    }

}