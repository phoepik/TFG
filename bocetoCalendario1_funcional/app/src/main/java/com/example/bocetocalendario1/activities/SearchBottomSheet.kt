package com.example.bocetocalendario1.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bocetocalendario1.R
import com.example.bocetocalendario1.adaptadores.EventoAdapter
import com.example.bocetocalendario1.models.Evento
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_EVENTOS = "eventos"

        fun newInstance(eventos: List<Evento>): SearchBottomSheet {
            val sheet = SearchBottomSheet()
            val args = Bundle()
            args.putSerializable(ARG_EVENTOS, ArrayList(eventos))
            sheet.arguments = args
            return sheet
        }
    }

    private var eventos: List<Evento> = emptyList()

    private val recentSearches = listOf("Cumple de Diego", "Sprint planning", "Yoga")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        eventos = (arguments?.getSerializable(ARG_EVENTOS) as? ArrayList<Evento>) ?: emptyList()

        val etSearch = view.findViewById<EditText>(R.id.etSearchQuery)
        val layoutRecent = view.findViewById<LinearLayout>(R.id.layoutRecentSearches)
        val layoutRecentItems = view.findViewById<LinearLayout>(R.id.layoutRecentItems)
        val rvResults = view.findViewById<RecyclerView>(R.id.rvSearchResults)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layoutSearchEmpty)
        val tvEmptyQuery = view.findViewById<TextView>(R.id.tvEmptyQuery)

        rvResults.layoutManager = LinearLayoutManager(context)

        // Build recent searches
        recentSearches.forEach { term ->
            val item = layoutInflater.inflate(R.layout.item_recent_search, layoutRecentItems, false)
            item.findViewById<TextView>(R.id.tvRecentTerm).text = term
            item.setOnClickListener {
                etSearch.setText(term)
                etSearch.setSelection(term.length)
            }
            layoutRecentItems.addView(item)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                if (query.isEmpty()) {
                    layoutRecent.visibility = View.VISIBLE
                    rvResults.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                } else {
                    layoutRecent.visibility = View.GONE
                    val results = eventos.filter {
                        it.titulo.contains(query, ignoreCase = true) ||
                                it.ubicacion.contains(query, ignoreCase = true)
                    }
                    if (results.isEmpty()) {
                        rvResults.visibility = View.GONE
                        layoutEmpty.visibility = View.VISIBLE
                        tvEmptyQuery.text = "Sin resultados para \"$query\""
                    } else {
                        rvResults.visibility = View.VISIBLE
                        layoutEmpty.visibility = View.GONE
                        rvResults.adapter = EventoAdapter(results, onClick = { evento ->
                            dismiss()
                            val sheet = EventDetalleBottomSheet.newInstance(evento)
                            sheet.show(parentFragmentManager, "EventDetalle")
                        })
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etSearch.requestFocus()
    }
}
