# Combine Framework

> Apple's reactive programming framework for processing values over time.

## Overview
Combine provides a declarative Swift API for processing async events and data streams, underpinning SwiftUI's reactivity.

## Core Concepts

### Publisher
Emits values over time.

### Subscriber
Receives values from a Publisher.

### Operator
Transforms, filters, or combines values.

## Basic Publishers

```swift
import Combine

// Just - single value
let just = Just("Hello")

// Future - async single value
let future = Future<String, Error> { promise in
    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
        promise(.success("Delayed value"))
    }
}

// PassthroughSubject - manual publishing
let subject = PassthroughSubject<Int, Never>()
subject.send(1)
subject.send(2)

// CurrentValueSubject - has current value
let currentValue = CurrentValueSubject<String, Never>("initial")
print(currentValue.value) // "initial"
currentValue.send("updated")
```

## Common Operators

```swift
let numbers = [1, 2, 3, 4, 5].publisher

// Transform
numbers
    .map { $0 * 2 }
    .sink { print($0) } // 2, 4, 6, 8, 10

// Filter
numbers
    .filter { $0 > 2 }
    .sink { print($0) } // 3, 4, 5

// Combine latest
let a = CurrentValueSubject<Int, Never>(1)
let b = CurrentValueSubject<Int, Never>(10)

Publishers.CombineLatest(a, b)
    .map { $0 + $1 }
    .sink { print($0) } // 11

// Debounce (search field)
$searchText
    .debounce(for: .milliseconds(300), scheduler: RunLoop.main)
    .removeDuplicates()
    .sink { query in
        self.search(query)
    }
```

## With SwiftUI

```swift
class SearchViewModel: ObservableObject {
    @Published var searchText = ""
    @Published var results: [Item] = []
    
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        $searchText
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main)
            .removeDuplicates()
            .filter { !$0.isEmpty }
            .flatMap { query in
                self.searchAPI(query: query)
                    .catch { _ in Just([]) }
            }
            .receive(on: DispatchQueue.main)
            .assign(to: &$results)
    }
    
    private func searchAPI(query: String) -> AnyPublisher<[Item], Error> {
        // Network request
    }
}
```

## Networking with Combine

```swift
struct APIClient {
    func fetch<T: Decodable>(_ type: T.Type, from url: URL) -> AnyPublisher<T, Error> {
        URLSession.shared.dataTaskPublisher(for: url)
            .map(\.data)
            .decode(type: T.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
}

// Usage
apiClient.fetch([User].self, from: usersURL)
    .sink(
        receiveCompletion: { completion in
            if case .failure(let error) = completion {
                print("Error: \(error)")
            }
        },
        receiveValue: { users in
            self.users = users
        }
    )
    .store(in: &cancellables)
```

## Memory Management

```swift
class ViewModel: ObservableObject {
    private var cancellables = Set<AnyCancellable>()
    
    func subscribe() {
        somePublisher
            .sink { [weak self] value in
                self?.handle(value)
            }
            .store(in: &cancellables) // Prevents memory leaks
    }
}
```

## Combine vs async/await

| Combine | async/await |
|---------|-------------|
| Multiple values over time | Single value |
| Complex transformations | Simple sequences |
| Reactive streams | Imperative async |
| Older iOS support | iOS 15+ |

## Related Patterns
- [[ObservableObject]] - Built on Combine
- [[Swift-Concurrency]] - Modern alternative
- [[Redux-Pattern]] - State streams

## Tags
#combine #reactive #async #streams #functional
