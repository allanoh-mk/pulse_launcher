# Pulse Search — Multi-Source Natural Language Search

## Goal
One search engine, multiple entry points, grouped card results.

## Sources
- apps and shortcuts
- Pulse Music
- contacts
- user-authorized files
- calculator
- live currency conversion
- Pulse settings
- Android settings intents where supported
- internet providers
- Feed providers

## Pipeline
```text
query → normalize → classify intent → dispatch sources
      → rank → deduplicate → card composer → render
```

## Source interface
```kotlin
interface SearchSource {
    val id: String
    suspend fun search(query: SearchQuery): List<SearchResult>
}
```

## Rules
Local results return first; network results stream in later. Cancelled queries cancel source jobs. Currency results show freshness and fall back to timestamped cached rates. Contacts and files are opt-in. File search uses Android-supported user-granted access and media APIs.