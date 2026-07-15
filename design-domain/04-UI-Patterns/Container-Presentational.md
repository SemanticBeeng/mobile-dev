# Container-Presentational Pattern

> Separating data/logic concerns from pure presentation.

## Overview
Also known as Smart/Dumb or Stateful/Stateless components, this pattern separates views into containers (logic) and presentational (UI) components.

## The Pattern

### Container View (Smart)
- Holds state and logic
- Fetches data
- Handles actions
- Passes data to presentational views

### Presentational View (Dumb)
- Pure rendering
- No state management (except UI state)
- Receives data via parameters
- Reports events via callbacks

## Implementation

```swift
// ========== PRESENTATIONAL (Dumb) ==========

struct UserListContent: View {
    let users: [User]
    let isLoading: Bool
    let onUserTap: (User) -> Void
    let onRefresh: () async -> Void
    
    var body: some View {
        Group {
            if isLoading && users.isEmpty {
                ProgressView()
            } else {
                List(users) { user in
                    UserRow(user: user)
                        .onTapGesture { onUserTap(user) }
                }
                .refreshable { await onRefresh() }
            }
        }
    }
}

struct UserRow: View {
    let user: User
    
    var body: some View {
        HStack {
            AsyncImage(url: user.avatarURL) { image in
                image.resizable()
            } placeholder: {
                Circle().fill(.gray.opacity(0.3))
            }
            .frame(width: 40, height: 40)
            .clipShape(Circle())
            
            VStack(alignment: .leading) {
                Text(user.name)
                    .font(.headline)
                Text(user.email)
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
        }
    }
}

// ========== CONTAINER (Smart) ==========

struct UserListContainer: View {
    @State private var viewModel = UserListViewModel()
    @Environment(AppCoordinator.self) private var coordinator
    
    var body: some View {
        UserListContent(
            users: viewModel.users,
            isLoading: viewModel.isLoading,
            onUserTap: { user in
                coordinator.navigate(to: .userDetail(user))
            },
            onRefresh: {
                await viewModel.refresh()
            }
        )
        .task {
            await viewModel.loadUsers()
        }
    }
}

@Observable
class UserListViewModel {
    private(set) var users: [User] = []
    private(set) var isLoading = false
    
    private let repository: UserRepository
    
    init(repository: UserRepository = UserRepositoryImpl()) {
        self.repository = repository
    }
    
    func loadUsers() async {
        isLoading = true
        users = await repository.fetchAll()
        isLoading = false
    }
    
    func refresh() async {
        await loadUsers()
    }
}
```

## Benefits

### Testability
```swift
// Presentational views are easy to preview
struct UserListContent_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            // Loading state
            UserListContent(
                users: [],
                isLoading: true,
                onUserTap: { _ in },
                onRefresh: { }
            )
            
            // Populated state
            UserListContent(
                users: User.mockList,
                isLoading: false,
                onUserTap: { _ in },
                onRefresh: { }
            )
            
            // Empty state
            UserListContent(
                users: [],
                isLoading: false,
                onUserTap: { _ in },
                onRefresh: { }
            )
        }
    }
}
```

### Reusability
```swift
// Same presentational view, different data source
struct SearchResultsContainer: View {
    @State private var searchResults: [User] = []
    
    var body: some View {
        UserListContent(
            users: searchResults,
            isLoading: false,
            onUserTap: handleTap,
            onRefresh: { }
        )
    }
}
```

## When to Split

✅ **Split when:**
- View has complex logic or async operations
- Multiple data sources render similarly
- Need extensive preview/test coverage

❌ **Don't split when:**
- View is simple and self-contained
- No reuse anticipated
- Would create unnecessary abstraction

## Naming Convention

| Container | Presentational |
|-----------|----------------|
| `UserListContainer` | `UserListContent` |
| `ProductDetailScreen` | `ProductDetailView` |
| `CartPage` | `CartLayout` |

## Related Patterns
- [[MVVM]] - ViewModel as container logic
- [[View-Composition]] - Building presentational views
- [[Coordinator-Pattern]] - Navigation in containers

## Tags
#ui-patterns #swiftui #separation #testing #composition
