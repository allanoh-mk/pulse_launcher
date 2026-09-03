package app.lawnchair.pulse.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(private val context: Context) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()
    private var searchJob: Job? = null

    private val dispatcher = SearchDispatcher(context)

    fun setVisible(visible: Boolean) {
        _state.update {
            it.copy(
                isVisible = visible,
                query = if (visible) it.query else "",
                results = if (visible) it.results else emptyList(),
            )
        }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, isLoading = true) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(results = emptyList(), isLoading = false) }
            return
        }
        searchJob = viewModelScope.launch {
            // Debounce
            delay(200)
            val results = dispatcher.search(query)
            _state.update { it.copy(results = results, isLoading = false) }
        }
    }
}
