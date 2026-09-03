package app.lawnchair.pulse.search

data class SearchState(
    val isVisible: Boolean = false,
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
)

data class SearchResult(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val type: ResultType,
    val actionData: String? = null,
)

enum class ResultType {
    APP,
    CONTACT,
    FILE,
    WEB,
    CALCULATION,
    SHORTCUT,
}
