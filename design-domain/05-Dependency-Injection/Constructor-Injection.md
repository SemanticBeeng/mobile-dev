# Constructor Injection

> The most explicit form of dependency injection via initializer parameters.

## Overview
Constructor (initializer) injection passes dependencies when creating an object, making dependencies explicit and the object immediately usable.

## Basic Pattern

```swift
// Dependencies defined as protocols
protocol UserRepository {
    func fetchUser(id: UUID) async throws -> User
}

protocol AnalyticsService {
    func track(event: String, properties: [String: Any])
}

// ViewModel with injected dependencies
@Observable
class ProfileViewModel {
    private let repository: UserRepository
    private let analytics: AnalyticsService
    
    // Constructor injection
    init(repository: UserRepository, analytics: AnalyticsService) {
        self.repository = repository
        self.analytics = analytics
    }
    
    func loadProfile(userId: UUID) async {
        analytics.track(event: "profile_viewed", properties: ["user_id": userId.uuidString])
        // Use repository...
    }
}
```

## Default Values for Convenience

```swift
@Observable
class ProfileViewModel {
    private let repository: UserRepository
    private let analytics: AnalyticsService
    
    init(
        repository: UserRepository = APIUserRepository(),
        analytics: AnalyticsService = FirebaseAnalytics()
    ) {
        self.repository = repository
        self.analytics = analytics
    }
}

// Usage
let viewModel = ProfileViewModel()  // Uses defaults
let testVM = ProfileViewModel(repository: MockRepository(), analytics: MockAnalytics())
```

## Factory Pattern for Complex Construction

```swift
struct ViewModelFactory {
    private let apiClient: APIClient
    private let database: Database
    private let analytics: AnalyticsService
    
    init(apiClient: APIClient, database: Database, analytics: AnalyticsService) {
        self.apiClient = apiClient
        self.database = database
        self.analytics = analytics
    }
    
    func makeProfileViewModel() -> ProfileViewModel {
        ProfileViewModel(
            repository: APIUserRepository(client: apiClient, cache: database),
            analytics: analytics
        )
    }
    
    func makeSettingsViewModel() -> SettingsViewModel {
        SettingsViewModel(
            preferences: UserPreferences(database: database),
            analytics: analytics
        )
    }
}
```

## SwiftUI Integration

```swift
struct ProfileView: View {
    @State private var viewModel: ProfileViewModel
    
    init(viewModel: ProfileViewModel) {
        _viewModel = State(initialValue: viewModel)
    }
    
    var body: some View {
        // View content
    }
}

// At composition root
struct AppView: View {
    let factory: ViewModelFactory
    
    var body: some View {
        NavigationStack {
            ProfileView(viewModel: factory.makeProfileViewModel())
        }
    }
}
```

## Testing Benefits

```swift
// Test file
class MockUserRepository: UserRepository {
    var users: [UUID: User] = [:]
    
    func fetchUser(id: UUID) async throws -> User {
        guard let user = users[id] else {
            throw RepositoryError.notFound
        }
        return user
    }
}

class MockAnalytics: AnalyticsService {
    var trackedEvents: [(event: String, properties: [String: Any])] = []
    
    func track(event: String, properties: [String: Any]) {
        trackedEvents.append((event, properties))
    }
}

@Test func testProfileLoadsAndTracksAnalytics() async {
    // Arrange
    let mockRepo = MockUserRepository()
    let mockAnalytics = MockAnalytics()
    let userId = UUID()
    mockRepo.users[userId] = User(id: userId, name: "Test")
    
    let viewModel = ProfileViewModel(repository: mockRepo, analytics: mockAnalytics)
    
    // Act  
    await viewModel.loadProfile(userId: userId)
    
    // Assert
    #expect(mockAnalytics.trackedEvents.count == 1)
    #expect(mockAnalytics.trackedEvents.first?.event == "profile_viewed")
}
```

## Advantages & Disadvantages

| Advantages | Disadvantages |
|------------|---------------|
| Explicit dependencies | Can lead to long init signatures |
| Compile-time safety | Manual wiring at composition root |
| Easy to test | Deep dependency graphs tedious |
| No hidden state | |

## Related Patterns
- [[Environment-Injection]] - SwiftUI alternative
- [[DI-Containers]] - Automated wiring
- [[Protocol-Oriented-Dependencies]] - What gets injected

## Tags
#dependency-injection #constructor #testing #initialization #explicit
