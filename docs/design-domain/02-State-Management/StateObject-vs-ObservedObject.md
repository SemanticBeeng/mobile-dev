# StateObject vs ObservedObject

> Understanding state ownership semantics in SwiftUI.

## Overview
The critical difference is **ownership**: who creates and owns the lifecycle of the object.

## Quick Reference

| Wrapper | Ownership | Creation | Survives View Recreation |
|---------|-----------|----------|-------------------------|
| @StateObject | View owns | View creates | ✅ Yes |
| @ObservedObject | External | Passed in | ❌ No (can be lost) |

## @StateObject - You Create It

Use when the **current view** is responsible for creating the object.

```swift
struct ParentView: View {
    // ✅ StateObject: We create and own this ViewModel
    @StateObject private var viewModel = ProductListViewModel()
    
    var body: some View {
        ProductListContent(viewModel: viewModel)
    }
}
```

### Lifecycle Guarantee
- Created **once** when view first appears
- Survives view re-renders
- Destroyed when view is removed from hierarchy

## @ObservedObject - Someone Else Creates It

Use when the object is **passed to you** from a parent.

```swift
struct ProductListContent: View {
    // ✅ ObservedObject: Parent passed this to us
    @ObservedObject var viewModel: ProductListViewModel
    
    var body: some View {
        List(viewModel.products) { product in
            ProductRow(product: product)
        }
    }
}
```

### No Lifecycle Guarantee
- View doesn't control object lifecycle
- Expects object to outlive the view

## Common Mistake ❌

```swift
struct BrokenView: View {
    // ❌ WRONG: ObservedObject recreated on every render
    @ObservedObject var viewModel = BrokenViewModel()
    
    var body: some View {
        Text("\(viewModel.count)") // Resets unexpectedly!
    }
}
```

When parent re-renders, `BrokenView` recreates `viewModel`.

## iOS 17+ with @Observable

```swift
@Observable
class ModernViewModel {
    var count = 0
}

struct ModernView: View {
    // @State for owned objects with @Observable
    @State private var viewModel = ModernViewModel()
    
    var body: some View {
        Text("\(viewModel.count)")
    }
}

struct ChildView: View {
    // Just regular property for passed objects
    var viewModel: ModernViewModel
    
    var body: some View {
        Text("\(viewModel.count)")
    }
}
```

## Decision Tree

```
Do I create this object?
├── YES → @StateObject (or @State with @Observable)
└── NO (passed from parent) → @ObservedObject (or plain property)
```

## Related Patterns
- [[ObservableObject]] - The protocol being observed
- [[EnvironmentObject]] - Another injection method
- [[State-and-Binding]] - Value type state

## Tags
#state-management #swiftui #ownership #lifecycle #property-wrappers
