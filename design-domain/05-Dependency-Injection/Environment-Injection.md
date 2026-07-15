# Environment Injection

> Leveraging SwiftUI's environment for dependency injection.

## Overview
SwiftUI's environment system provides an elegant way to inject dependencies down the view hierarchy without prop drilling.

## Built-in Environment Values

```swift
struct MyView: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.dismiss) var dismiss
    @Environment(\.openURL) var openURL
    @Environment(\.locale) var locale
    @Environment(\.calendar) var calendar
    
    var body: some View {
        Button("Dismiss") {
            dismiss()
        }
    }
}
```

## Custom Environment Keys

```swift
// 1. Define the key
struct APIClientKey: EnvironmentKey {
    static let defaultValue: APIClient = URLSessionAPIClient()
}

// 2. Extend EnvironmentValues
extension EnvironmentValues {
    var apiClient: APIClient {
        get { self[APIClientKey.self] }
        set { self[APIClientKey.self] = newValue }
    }
}

// 3. Usage
struct ProductListView: View {
    @Environment(\.apiClient) var apiClient
    
    var body: some View {
        // Use apiClient
    }
}

// 4. Injection
ContentView()
    .environment(\.apiClient, MockAPIClient())
```

## Protocol-Based Environment Dependencies

```swift
protocol AuthService {
    var isLoggedIn: Bool { get }
    func login(email: String, password: String) async throws
    func logout() async
}

struct AuthServiceKey: EnvironmentKey {
    static let defaultValue: AuthService = FirebaseAuthService()
}

extension EnvironmentValues {
    var authService: AuthService {
        get { self[AuthServiceKey.self] }
        set { self[AuthServiceKey.self] = newValue }
    }
}

// In tests/previews
struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
            .environment(\.authService, MockAuthService(isLoggedIn: false))
    }
}
```

## iOS 17+ Observable Environment

```swift
@Observable
class AppDependencies {
    let apiClient: APIClient
    let authService: AuthService
    let analytics: AnalyticsService
    
    init(
        apiClient: APIClient = URLSessionAPIClient(),
        authService: AuthService = FirebaseAuth(),
        analytics: AnalyticsService = MixpanelAnalytics()
    ) {
        self.apiClient = apiClient
        self.authService = authService
        self.analytics = analytics
    }
}

// Inject as Observable
@main
struct MyApp: App {
    @State private var dependencies = AppDependencies()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(dependencies)
        }
    }
}

// Access
struct SomeView: View {
    @Environment(AppDependencies.self) var deps
    
    var body: some View {
        // Use deps.apiClient, deps.authService, etc.
    }
}
```

## Repository Pattern with Environment

```swift
protocol UserRepository {
    func fetchAll() async throws -> [User]
}

struct UserRepositoryKey: EnvironmentKey {
    static let defaultValue: UserRepository = APIUserRepository()
}

extension EnvironmentValues {
    var userRepository: UserRepository {
        get { self[UserRepositoryKey.self] }
        set { self[UserRepositoryKey.self] = newValue }
    }
}

struct UserListView: View {
    @Environment(\.userRepository) var repository
    @State private var users: [User] = []
    
    var body: some View {
        List(users) { user in
            Text(user.name)
        }
        .task {
            users = (try? await repository.fetchAll()) ?? []
        }
    }
}
```

## Testing Pattern

```swift
struct ContentView_Tests {
    @Test func testViewWithMockDependencies() {
        let mockAPIClient = MockAPIClient()
        let mockAuth = MockAuthService()
        
        let view = ContentView()
            .environment(\.apiClient, mockAPIClient)
            .environment(\.authService, mockAuth)
        
        // Use ViewInspector or snapshot tests
    }
}
```

## Best Practices

1. **Use protocols** for environment dependencies
2. **Provide sensible defaults** in EnvironmentKey
3. **Keep dependency container small** - split if needed
4. **Inject at root** - minimize scattered injections

## Environment vs EnvironmentObject

| Environment | EnvironmentObject |
|-------------|-------------------|
| Value types & protocols | ObservableObject only |
| Custom keys | Type-based lookup |
| More flexible | Simpler API |
| iOS 17+ for Observable | iOS 13+ |

## Related Patterns
- [[Constructor-Injection]] - Alternative injection style
- [[Protocol-Oriented-Dependencies]] - What gets injected
- [[EnvironmentObject]] - ObservableObject variant

## Tags
#dependency-injection #swiftui #environment #testing #inversion
