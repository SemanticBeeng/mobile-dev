# Coordinator Pattern

> Managing navigation flow and decoupling view presentation logic.

## Overview
The Coordinator pattern extracts navigation logic from views, enabling complex flows, deep linking, and testable navigation.

## Why Coordinators?

- **Separation of Concerns**: Views don't know about navigation
- **Flow Management**: Multi-step flows (onboarding, checkout)
- **Deep Linking**: Central place to handle external URLs
- **Testability**: Navigation logic can be tested

## SwiftUI Coordinator Implementation

```swift
// Coordinator Protocol
protocol Coordinator: ObservableObject {
    associatedtype Route: Hashable
    var path: NavigationPath { get set }
    func navigate(to route: Route)
    func pop()
    func popToRoot()
}

// App Coordinator
@Observable
final class AppCoordinator: Coordinator {
    var path = NavigationPath()
    
    enum Route: Hashable {
        case productList
        case productDetail(Product)
        case cart
        case checkout
        case orderConfirmation(Order)
    }
    
    func navigate(to route: Route) {
        path.append(route)
    }
    
    func pop() {
        path.removeLast()
    }
    
    func popToRoot() {
        path.removeLast(path.count)
    }
}
```

## Coordinator View

```swift
struct CoordinatedNavigationView: View {
    @State private var coordinator = AppCoordinator()
    
    var body: some View {
        NavigationStack(path: $coordinator.path) {
            ProductListView()
                .navigationDestination(for: AppCoordinator.Route.self) { route in
                    destinationView(for: route)
                }
        }
        .environment(coordinator)
    }
    
    @ViewBuilder
    private func destinationView(for route: AppCoordinator.Route) -> some View {
        switch route {
        case .productList:
            ProductListView()
        case .productDetail(let product):
            ProductDetailView(product: product)
        case .cart:
            CartView()
        case .checkout:
            CheckoutView()
        case .orderConfirmation(let order):
            OrderConfirmationView(order: order)
        }
    }
}
```

## Using Coordinator in Views

```swift
struct ProductListView: View {
    @Environment(AppCoordinator.self) private var coordinator
    let products: [Product]
    
    var body: some View {
        List(products) { product in
            ProductRow(product: product)
                .onTapGesture {
                    coordinator.navigate(to: .productDetail(product))
                }
        }
        .toolbar {
            Button {
                coordinator.navigate(to: .cart)
            } label: {
                Image(systemName: "cart")
            }
        }
    }
}

struct ProductDetailView: View {
    @Environment(AppCoordinator.self) private var coordinator
    let product: Product
    
    var body: some View {
        VStack {
            // Product details
            Button("Add to Cart & Checkout") {
                CartManager.shared.add(product)
                coordinator.navigate(to: .checkout)
            }
        }
    }
}
```

## Flow Coordinators (Sub-Coordinators)

```swift
@Observable
final class OnboardingCoordinator: Coordinator {
    var path = NavigationPath()
    var onComplete: () -> Void = {}
    
    enum Route: Hashable {
        case welcome
        case permissions
        case profile
        case complete
    }
    
    func navigate(to route: Route) {
        if route == .complete {
            onComplete()
        } else {
            path.append(route)
        }
    }
}

// Parent coordinator starts onboarding
func startOnboarding() {
    let onboarding = OnboardingCoordinator()
    onboarding.onComplete = { [weak self] in
        self?.onboardingComplete = true
    }
    // Present onboarding flow
}
```

## Deep Linking

```swift
extension AppCoordinator {
    func handle(url: URL) {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
              let host = components.host else { return }
        
        switch host {
        case "product":
            if let id = components.queryItems?.first(where: { $0.name == "id" })?.value {
                Task {
                    let product = await ProductService.shared.fetch(id: id)
                    navigate(to: .productDetail(product))
                }
            }
        case "cart":
            navigate(to: .cart)
        default:
            break
        }
    }
}
```

## Sheet/Modal Coordination

```swift
@Observable
final class ModalCoordinator {
    var presentedSheet: Sheet?
    var presentedFullScreen: FullScreenCover?
    
    enum Sheet: Identifiable {
        case settings
        case filter
        case share(item: ShareItem)
        
        var id: String { String(describing: self) }
    }
    
    enum FullScreenCover: Identifiable {
        case camera
        case imageViewer(images: [Image])
        
        var id: String { String(describing: self) }
    }
}

// In view
.sheet(item: $modalCoordinator.presentedSheet) { sheet in
    switch sheet {
    case .settings: SettingsView()
    case .filter: FilterView()
    case .share(let item): ShareSheet(item: item)
    }
}
```

## Related Patterns
- [[VIPER]] - Router component
- [[Clean-Architecture]] - Presentation layer
- [[MVVM]] - ViewModel coordination

## Tags
#navigation #swiftui #coordinator #flow #deep-linking
