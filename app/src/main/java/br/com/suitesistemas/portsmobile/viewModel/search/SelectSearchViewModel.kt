package br.com.suitesistemas.portsmobile.viewModel.search

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData

abstract class SelectSearchViewModel<T> : SearchViewModel<T>() {

    @Bindable val selected = MutableLiveData<Boolean>()
    private val objsSelected: MutableList<T> = mutableListOf()
    private lateinit var selectedResponse: (Boolean) -> Unit

    init {
        selected.value = false
    }

    fun onSelected(selected: (Boolean) -> Unit) {
        selectedResponse = selected
    }

    fun getSelectedList(): MutableList<T> = getListCopy(objsSelected)

    fun onChecked(checked: Boolean, obj: T) {
        when (checked) {
            true -> objsSelected.add(obj)
            false -> objsSelected.remove(obj)
        }
        selected.value = objsSelected.isNotEmpty()
        selectedResponse.invoke(objsSelected.isNotEmpty())
    }

}