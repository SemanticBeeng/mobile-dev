# ObservableObject

> The foundation of reference-type state management in SwiftUI (pre-iOS 17).

## Overview
`ObservableObject` is a protocol for classes that publish changes, allowing SwiftUI to automatically update views.

## Core Components

### @Published
Property wrapper that emits changes.

### objectWillChange
Publisher that fires before any `@Published` property changes.

## Basic Usage

```swift
class UserSettings: ObservableObject {
    @Published var username: String = ""
    @Published var isDarkMode: Bool = false
    @Published var notificationsEnabled: Bool = true
}

struct SettingsView: View {
    @ObservedObject var settings: UserSettings
    
    var body: some View {
        Form {
            TextField("Username", text: $settings.username)
            Toggle("Dark Mode", isOn: $settings.isDarkMode)
            Toggle("Notifications", isOn: $settings.notificationsEnabled)
        }
    }
}
```

## Custom Publishers

```swift
class TimerViewModel: ObservableObject {
    @Published private(set) var count: Int = 0
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        Timer.publish(every: 1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                self?.count += 1
            }
            .store(in: &cancellables)
    }
}
```

## Manual Change Notification

```swift
class DataManager: ObservableObject {
    var items: [Item] = [] {
        willSet {
            objectWillChange.send()
        }
    }
    
    // Or for complex updates
    func updateItems(_ newItems: [Item]) {
        objectWillChange.send()
        items = newItems
    }
}
```

## iOS 17+ Migration to @Observable

```swift
// Before (ObservableObject)
class ViewModel: ObservableObject {
    @Published var name: String = ""
}

struct MyView: View {
    @StateObject var viewModel = ViewModel()
    var body: some View {
        TextField("Name", text: $viewModel.name)
    }
}

// After (@Observable macro)
@Observable
class ViewModel {
    var name: String = ""
}

struct MyView: View {
    @State var viewModel = ViewModel()
    var body: some View {
        TextField("Name", text: $viewModel.name)
    }
}
```

## Key Differences: @Observable vs ObservableObject

| ObservableObject | @Observable (iOS 17+) |
|------------------|----------------------|
| Whole object observation | Per-property observation |
| Requires @Published | Automatic tracking |
| @StateObject/@ObservedObject | @State/@Bindable |
| Manual binding with $ | @Bindable for bindings |

## Related Patterns
- [[StateObject-vs-ObservedObject]] - Ownership semantics
- [[EnvironmentObject]] - Global state injection
- [[Combine-Framework]] - Underlying reactive system

## Tags
#state-management #swiftui #observable #combine #reactive
