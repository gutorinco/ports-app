package br.com.suitesistemas.portsmobile.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.suitesistemas.portsmobile.R

class TaskFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onResume() {
        super.onResume()
        configureTitle()
    }

    private fun configureTitle() {
        activity?.setTitle(R.string.tarefas)
    }

}
