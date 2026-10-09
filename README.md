# Orbit

A spaceflight news reader for Android, built to showcase **Paging 3** with a clean MVVM + Hilt architecture. Orbit streams articles from the [Spaceflight News API](https://www.spaceflightnewsapi.net/) with infinite scroll, debounced search, and LoadState-driven UI — no API key required.

## Features

- Infinite-scrolling feed of spaceflight news articles (Paging 3, offset-based paging)
- Debounced search (300 ms) that restarts paging with the new query
- Relative timestamps ("2h ago") with an absolute-date fallback
- Full LoadState handling: initial-load spinner, full-screen error with retry, append footer spinner, and inline append-error retry
- Article images via Coil
- Articles open in Chrome Custom Tabs
- Edge-to-edge Material 3 UI with light/dark theme

## Architecture

Single-module MVVM with unidirectional data flow (UDF):

```
SpaceflightApi (Retrofit + kotlinx.serialization)
        │
ArticlesPagingSource (offset-based PagingSource<Int, Article>)
        │
ArticlesRepository ──▶ Pager(pageSize = 20) ──▶ Flow<PagingData<Article>>
        │
FeedViewModel (query StateFlow → debounce → flatMapLatest → cachedIn)
        │
FeedScreen (stateless; collectAsLazyPagingItems + LoadState-driven UI)
```

### Why `PagingSource` instead of `RemoteMediator`?

`RemoteMediator` earns its complexity when paged data must be mirrored into a local cache (Room) and served offline. Orbit's product requirement is a live news feed with no offline mandate, so a plain offset-based `PagingSource` is the honest choice: one layer fewer, no cache-invalidation edge cases, and the API's `limit`/`offset` contract maps directly onto load keys (`nextKey = offset + results.size`, terminated by a short page or a null `next` link). If offline reading lands on the roadmap, the repository interface stays stable and only the Pager wiring changes.

### UDF and LoadState-driven UI

State flows down (query → repository → `PagingData` → UI) and events flow up (text changes, retry taps). The UI never guesses at loading status: it renders exclusively from `CombinedLoadStates` — `refresh` drives the full-screen states, `append` drives the footer — so every spinner and retry button is backed by the paging library's actual state machine.

### Testing seams

Every boundary is an interface with a hand-written fake: `FakeSpaceflightApi` exercises the `PagingSource` key math and error mapping, and a fake `ArticlesRepository` verifies the ViewModel's debounce/`flatMapLatest` behavior under virtual time.

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Kotlin 2.1 |
| UI | Jetpack Compose + Material 3 |
| Pagination | Paging 3 (`paging-compose`) |
| DI | Hilt |
| Networking | Retrofit + OkHttp + kotlinx.serialization |
| Images | Coil |
| Async | Kotlin Coroutines + Flow |
| In-app browsing | AndroidX Browser (Chrome Custom Tabs) |
| Testing | JUnit 4, kotlinx-coroutines-test, Turbine, paging-common |
| Build | Gradle version catalog, AGP 8.7 |

## Getting started

No API key needed — the Spaceflight News API is free and unauthenticated.

1. Clone the repo
2. Open in Android Studio (or create `local.properties` pointing at your SDK)
3. Run the `app` configuration, or:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

## Roadmap

- Offline cache with Room + `RemoteMediator`
- Bookmarks
- Category/news-site filters
