# EnvironmentObject

> Dependency injection via SwiftUI's environment for shared state.

## Overview
EnvironmentObject allows passing dependencies down the view hierarchy without explicit prop drilling.

## Basic Usage

```swift
class AppState: ObservableObject {
    @Published var isLoggedIn: Bool = false
    @Published var user: User?
    @Published var theme: Theme = .system
}

// Inject at root
@main
struct MyApp: App {
    @StateObject private var appState = AppState()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(appState)
        }
    }
}

// Access anywhere in subtree
struct ProfileView: View {
    @EnvironmentObject var appState: AppState
    
    var body: some View {
        if let user = appState.user {
            Text("Hello, \(user.name)")
        }
    }
}
```

## Multiple Environment Objects

```swift
class AuthService: ObservableObject { ... }
class CartService: ObservableObject { ... }
class SettingsService: ObservableObject { ... }

ContentView()
    .environmentObject(authService)
    .environmentObject(cartService)
    .environmentObject(settingsService)
```

## Environment vs EnvironmentObject

| @Environment | @EnvironmentObject |
|--------------|-------------------|
| System values | Custom objects |
| Value types | Reference types |
| Built-in keys | Type-based lookup |

```swift
// @Environment - system values
@Environment(\.colorScheme) var colorScheme
@Environment(\.dismiss) var dismiss

// @EnvironmentObject - custom objects
@EnvironmentObject var appState: AppState
```

## iOS 17+ with @Observable

```swift
@Observable
class ModernAppState {
    var user: User?
}

// Use @Environment with Observable
struct ModernView: View {
    @Environment(ModernAppState.self) var appState
    
    var body: some View {
        if let user = appState.user {
            Text(user.name)
        }
    }
}

// Inject
ContentView()
    .environment(ModernAppState())
```

## Testing with Environment

```swift
struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileView()
            .environmentObject(AppState.preview)
    }
}

extension AppState {
    static var preview: AppState {
        let state = AppState()
        state.user = User.mock
        return state
    }
}
```

## Pitfalls

### Missing EnvironmentObject
```swift
// ❌ Crash if EnvironmentObject not provided
struct SomeView: View {
    @EnvironmentObject var missing: SomeService // 💥 Runtime crash
}
```

### Optional Environment (iOS 17+)
```swift
@Environment(OptionalService.self) var service: OptionalService?
```

## Best Practices
1. Inject at app/scene level
2. Keep environment objects focused
3. Always provide in previews
4. Consider protocols for testability

## Related Patterns
- [[ObservableObject]] - The protocol for environment objects
- [[Environment-Injection]] - The DI pattern
- [[Protocol-Oriented-Dependencies]] - Abstracting for tests

## Tags
#state-management #swiftui #dependency-injection #environment #global-state
