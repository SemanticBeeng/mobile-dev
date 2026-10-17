# State and Binding

> SwiftUI's fundamental value-type state management primitives.

## Overview
`@State` and `@Binding` manage value types in SwiftUI, providing the foundation for reactive UI updates.

## @State - Local View State

Owns and manages a value type within a single view.

```swift
struct CounterView: View {
    @State private var count = 0
    
    var body: some View {
        VStack {
            Text("Count: \(count)")
            Button("Increment") {
                count += 1
            }
        }
    }
}
```

### Key Characteristics
- **Private** by convention (implementation detail)
- **Value types** (Int, String, Bool, structs)
- **View-owned** lifecycle
- **Persists** across view re-renders

## @Binding - Borrowed Reference

Creates a two-way connection to state owned elsewhere.

```swift
struct ToggleRow: View {
    let title: String
    @Binding var isOn: Bool  // Borrowed from parent
    
    var body: some View {
        Toggle(title, isOn: $isOn)
    }
}

struct SettingsView: View {
    @State private var wifiEnabled = true
    @State private var bluetoothEnabled = false
    
    var body: some View {
        List {
            ToggleRow(title: "WiFi", isOn: $wifiEnabled)
            ToggleRow(title: "Bluetooth", isOn: $bluetoothEnabled)
        }
    }
}
```

## The $ Prefix

Creates a `Binding` from a `@State` property.

```swift
@State private var name = ""

TextField("Name", text: $name)  // $name is Binding<String>
```

## Binding Transformations

```swift
// Constant binding (read-only, for previews)
Toggle("Option", isOn: .constant(true))

// Custom binding
let uppercased = Binding(
    get: { name.uppercased() },
    set: { name = $0 }
)

// Binding to optional
@State private var selectedItem: Item?
if let item = Binding($selectedItem) {
    ItemEditor(item: item)
}
```

## State for Complex Value Types

```swift
struct FormData {
    var name = ""
    var email = ""
    var age = 0
}

struct FormView: View {
    @State private var formData = FormData()
    
    var body: some View {
        Form {
            TextField("Name", text: $formData.name)
            TextField("Email", text: $formData.email)
            Stepper("Age: \(formData.age)", value: $formData.age)
        }
    }
}
```

## When to Use What

| Scenario | Use |
|----------|-----|
| Simple local UI state | @State |
| Child needs to modify parent state | @Binding |
| Complex object with methods | @StateObject/@Observable |
| Shared across many views | @EnvironmentObject/@Environment |

## Common Patterns

### Toggleable Sheet
```swift
struct ContentView: View {
    @State private var showingSheet = false
    
    var body: some View {
        Button("Show Sheet") {
            showingSheet = true
        }
        .sheet(isPresented: $showingSheet) {
            SheetView()
        }
    }
}
```

### Form Validation
```swift
@State private var email = ""

var isValidEmail: Bool {
    email.contains("@") && email.contains(".")
}

Button("Submit")
    .disabled(!isValidEmail)
```

## Related Patterns
- [[ObservableObject]] - Reference type state
- [[StateObject-vs-ObservedObject]] - Object ownership
- [[View-Composition]] - Building with state

## Tags
#state-management #swiftui #binding #value-types #fundamentals
