# Redux Pattern

> Unidirectional data flow pattern for predictable state management.

## Overview
Redux provides a single source of truth with explicit state mutations, making state changes predictable and debuggable.

## Core Principles

1. **Single Source of Truth** - One store holds entire app state
2. **State is Read-Only** - Changes only via dispatched actions
3. **Pure Reducers** - State changes are pure functions

## Data Flow

```
     ┌─────────────────────────────────────┐
     │                                     │
     ▼                                     │
┌─────────┐    ┌──────────┐    ┌───────────┤
│  View   │───►│  Action  │───►│  Reducer  │
└─────────┘    └──────────┘    └───────────┘
     ▲                              │
     │         ┌─────────┐          │
     └─────────│  State  │◄─────────┘
               └─────────┘
```

## Swift Implementation

```swift
// State
struct AppState {
    var counter: Int = 0
    var todos: [Todo] = []
    var isLoading: Bool = false
}

// Actions
enum AppAction {
    case increment
    case decrement
    case addTodo(String)
    case removeTodo(IndexSet)
    case setLoading(Bool)
}

// Reducer
func appReducer(state: inout AppState, action: AppAction) {
    switch action {
    case .increment:
        state.counter += 1
    case .decrement:
        state.counter -= 1
    case .addTodo(let title):
        state.todos.append(Todo(title: title))
    case .removeTodo(let indexSet):
        state.todos.remove(atOffsets: indexSet)
    case .setLoading(let loading):
        state.isLoading = loading
    }
}

// Store
@Observable
final class Store<State, Action> {
    private(set) var state: State
    private let reducer: (inout State, Action) -> Void
    
    init(initial: State, reducer: @escaping (inout State, Action) -> Void) {
        self.state = initial
        self.reducer = reducer
    }
    
    func dispatch(_ action: Action) {
        reducer(&state, action)
    }
}

// Usage
let store = Store(initial: AppState(), reducer: appReducer)

struct ContentView: View {
    @Bindable var store: Store<AppState, AppAction>
    
    var body: some View {
        VStack {
            Text("\(store.state.counter)")
            Button("+") { store.dispatch(.increment) }
        }
    }
}
```

## Middleware for Side Effects

```swift
typealias Middleware<State, Action> = 
    (State, Action, @escaping (Action) -> Void) async -> Void

let loggingMiddleware: Middleware<AppState, AppAction> = { state, action, dispatch in
    print("Action: \(action)")
    print("State: \(state)")
}

let asyncMiddleware: Middleware<AppState, AppAction> = { state, action, dispatch in
    if case .fetchData = action {
        dispatch(.setLoading(true))
        let data = await fetchFromAPI()
        dispatch(.dataLoaded(data))
        dispatch(.setLoading(false))
    }
}
```

## Pros & Cons

| Pros | Cons |
|------|------|
| Predictable state | Verbose boilerplate |
| Time-travel debugging | Performance with large state |
| Easy to test | Action explosion |
| Centralized state | Learning curve |

## Related Patterns
- [[TCA]] - Swift-native Redux-inspired architecture
- [[ObservableObject]] - Store implementation
- [[State-and-Binding]] - Local state alternative

## Tags
#architecture #redux #state-management #unidirectional #flux
