package br.com.suitesistemas.portsmobile.view.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import br.com.suitesistemas.portsmobile.custom.extensions.unaccent

class AutoCompleteAdapter(context: Context,
                          layout: Int,
                          private val list: List<String>) : ArrayAdapter<String>(context, layout, list), Filterable {

    private var results = list
    private val filter = NoSimbolsFilter()

    override fun getCount(): Int = results.size

    override fun getFilter(): Filter = filter

    override fun getItem(position: Int): String? {
        if (!results.isNullOrEmpty() && position < results.size)
            return results[position]
        return null
    }

    inner class NoSimbolsFilter : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            val values = mutableListOf<String>()

            constraint.let {
                val term = it.toString().unaccent().toLowerCase()
                lateinit var placeString: String

                list.forEach { value ->
                    placeString = value.unaccent().toLowerCase()
                    if (placeString.indexOf(term) > -1)
                        values.add(value)
                }
            }

            with (filterResults) {
                this.values = values
                count = values.size
                return this
            }
        }

        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
            filterResults?.let {
                results = it.values as List<String>
                notifyDataSetChanged()
            }
        }

    }
}