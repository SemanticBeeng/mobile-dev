# TCA - The Composable Architecture

> A powerful, opinionated architecture by Point-Free for building SwiftUI apps.

## Overview
TCA provides a structured approach to state management, side effects, and composition with strong testing support.

## Core Concepts

### State
Immutable value type representing feature state.

### Action
Enum of all possible events/intents.

### Reducer
Pure function: `(State, Action) -> State + Effects`

### Store
Runtime that holds state and processes actions.

## Basic Implementation

```swift
import ComposableArchitecture

@Reducer
struct Counter {
    @ObservableState
    struct State: Equatable {
        var count = 0
    }
    
    enum Action {
        case increment
        case decrement
        case reset
    }
    
    var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .increment:
                state.count += 1
                return .none
            case .decrement:
                state.count -= 1
                return .none
            case .reset:
                state.count = 0
                return .none
            }
        }
    }
}

struct CounterView: View {
    let store: StoreOf<Counter>
    
    var body: some View {
        VStack {
            Text("\(store.count)")
            HStack {
                Button("-") { store.send(.decrement) }
                Button("+") { store.send(.increment) }
            }
        }
    }
}
```

## Side Effects

```swift
@Reducer
struct Feature {
    @Dependency(\.apiClient) var apiClient
    
    var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .loadData:
                return .run { send in
                    let data = try await apiClient.fetch()
                    await send(.dataLoaded(data))
                }
            case .dataLoaded(let data):
                state.data = data
                return .none
            }
        }
    }
}
```

## Composition
Reducers compose via `Scope` and child stores.

## Pros & Cons

| Pros | Cons |
|------|------|
| Highly testable | Steep learning curve |
| Predictable state | Verbose for simple features |
| Excellent composition | External dependency |
| Time-travel debugging | Performance overhead |

## Related Patterns
- [[Redux-Pattern]] - Inspiration for TCA
- [[State-and-Binding]] - Underlying SwiftUI state
- [[Swift-Concurrency]] - Effect execution

## Tags
#architecture #tca #composable #state-management #pointfree
