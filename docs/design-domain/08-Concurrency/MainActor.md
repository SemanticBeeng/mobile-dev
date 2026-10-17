# MainActor

> Ensuring code runs on the main thread for UI safety.

## Overview
`@MainActor` is a global actor that guarantees code execution on the main thread, essential for UI updates in iOS.

## Basic Usage

```swift
// Mark entire class
@MainActor
class ProfileViewModel: ObservableObject {
    @Published var user: User?
    @Published var isLoading = false
    
    func loadUser() async {
        isLoading = true  // Safe - on main thread
        user = try? await userRepository.fetch()
        isLoading = false
    }
}

// Mark individual functions
class DataService {
    @MainActor
    func updateUI(with data: Data) {
        // Guaranteed main thread
    }
}

// Mark properties
class ViewModel {
    @MainActor var displayText: String = ""
}
```

## SwiftUI Integration

```swift
// SwiftUI Views are implicitly @MainActor
struct UserView: View {
    @StateObject private var viewModel = UserViewModel()
    
    var body: some View {
        VStack {
            if viewModel.isLoading {
                ProgressView()
            } else if let user = viewModel.user {
                Text(user.name)
            }
        }
        .task {
            await viewModel.load()  // Async but UI updates happen on main
        }
    }
}
```

## Switching to MainActor

```swift
// From background work
func processData() async {
    let result = await heavyComputation()  // Background
    
    await MainActor.run {
        // Update UI here
        self.displayResult = result
    }
}

// Or use @MainActor function
@MainActor
func displayResult(_ result: Result) {
    self.result = result
    self.showAlert = true
}

func processData() async {
    let result = await heavyComputation()
    await displayResult(result)  // Hops to main
}
```

## MainActor.assumeIsolated

```swift
// When you KNOW you're on main thread but compiler doesn't
func callbackFromUIKit() {
    MainActor.assumeIsolated {
        // Safe to access @MainActor state
        viewModel.updateFromCallback()
    }
}
```

## ViewModels Pattern

```swift
@MainActor
@Observable
class ShoppingCartViewModel {
    private(set) var items: [CartItem] = []
    private(set) var isLoading = false
    private(set) var error: Error?
    
    private let cartService: CartService
    
    init(cartService: CartService) {
        self.cartService = cartService
    }
    
    func loadCart() async {
        isLoading = true
        defer { isLoading = false }
        
        do {
            items = try await cartService.fetchItems()
        } catch {
            self.error = error
        }
    }
    
    func addItem(_ product: Product) async {
        do {
            let newItem = try await cartService.add(product)
            items.append(newItem)  // UI update - safe!
        } catch {
            self.error = error
        }
    }
}
```

## Avoiding Main Thread Blocking

```swift
@MainActor
class ViewModel {
    func process() async {
        // ❌ Don't do heavy work on main
        let result = expensiveComputation()  
        
        // ✅ Offload to background
        let result = await Task.detached {
            self.expensiveComputation()
        }.value
        
        // Back on main for UI
        self.displayResult(result)
    }
}
```

## nonisolated Escape Hatch

```swift
@MainActor
class ViewModel {
    var title: String = ""
    
    // This function doesn't need main thread
    nonisolated func computeHash() -> String {
        // Can run on any thread
        // Cannot access self.title here!
        return UUID().uuidString
    }
    
    nonisolated var identifier: String {
        "ViewModel-\(ObjectIdentifier(self))"
    }
}
```

## Combine with MainActor

```swift
@MainActor
class SearchViewModel: ObservableObject {
    @Published var query = ""
    @Published var results: [Item] = []
    
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        $query
            .debounce(for: .milliseconds(300), scheduler: RunLoop.main)
            .removeDuplicates()
            .sink { [weak self] query in
                Task { @MainActor in
                    await self?.search(query)
                }
            }
            .store(in: &cancellables)
    }
}
```

## Related Patterns
- [[Actors]] - Custom actors
- [[Swift-Concurrency]] - async/await basics
- [[ObservableObject]] - UI state management

## Tags
#concurrency #mainactor #ui #thread-safety #swiftui
