# MVVM - Model-View-ViewModel

> The dominant architecture pattern for SwiftUI applications.

## Overview
MVVM separates presentation logic from UI, making code testable and maintainable.

## Components

### Model
- Pure data structures
- Business logic
- Domain entities

### View
- SwiftUI views
- Declarative UI
- No business logic

### ViewModel
- `@Observable` or `ObservableObject`
- Transforms model data for display
- Handles user actions

## SwiftUI Implementation

```swift
// Model
struct User: Identifiable {
    let id: UUID
    var name: String
    var email: String
}

// ViewModel
@Observable
class UserViewModel {
    private(set) var users: [User] = []
    var isLoading = false
    
    private let repository: UserRepository
    
    init(repository: UserRepository) {
        self.repository = repository
    }
    
    func loadUsers() async {
        isLoading = true
        users = await repository.fetchUsers()
        isLoading = false
    }
}

// View
struct UserListView: View {
    @State private var viewModel: UserViewModel
    
    init(viewModel: UserViewModel) {
        _viewModel = State(initialValue: viewModel)
    }
    
    var body: some View {
        List(viewModel.users) { user in
            Text(user.name)
        }
        .task { await viewModel.loadUsers() }
    }
}
```

## Pros & Cons

| Pros | Cons |
|------|------|
| Testable ViewModels | Can lead to massive ViewModels |
| Clear separation | Boilerplate for simple screens |
| SwiftUI native fit | State synchronization complexity |

## Related Patterns
- [[Clean-Architecture]] - MVVM as presentation layer
- [[Repository-Pattern]] - Data layer abstraction
- [[ObservableObject]] - State observation

## Tags
#architecture #mvvm #swiftui #presentation-layer
