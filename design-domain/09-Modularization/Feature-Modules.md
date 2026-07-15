# Feature Modules

> Organizing code by feature for scalable iOS architectures.

## Overview
Feature modules encapsulate related functionality (UI, logic, data) into self-contained units, enabling parallel development and better build times.

## Module Structure

```
App/
├── Package.swift
├── Sources/
│   └── App/
│       └── AppDelegate.swift
└── Modules/
    ├── Core/
    │   ├── Package.swift
    │   └── Sources/
    │       ├── Networking/
    │       ├── Extensions/
    │       └── Utilities/
    ├── Domain/
    │   ├── Package.swift
    │   └── Sources/
    │       ├── Entities/
    │       └── Repositories/
    ├── FeatureHome/
    │   ├── Package.swift
    │   └── Sources/
    │       ├── Views/
    │       ├── ViewModels/
    │       └── HomeModule.swift
    ├── FeatureProfile/
    │   ├── Package.swift
    │   └── Sources/
    │       ├── Views/
    │       ├── ViewModels/
    │       └── ProfileModule.swift
    └── FeatureSettings/
```

## Module Definition (Swift Package)

```swift
// Modules/FeatureHome/Package.swift
// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "FeatureHome",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "FeatureHome", targets: ["FeatureHome"])
    ],
    dependencies: [
        .package(path: "../Core"),
        .package(path: "../Domain"),
        .package(path: "../DesignSystem")
    ],
    targets: [
        .target(
            name: "FeatureHome",
            dependencies: ["Core", "Domain", "DesignSystem"]
        ),
        .testTarget(
            name: "FeatureHomeTests",
            dependencies: ["FeatureHome"]
        )
    ]
)
```

## Module Entry Point

```swift
// FeatureHome/Sources/HomeModule.swift
import SwiftUI
import Domain
import Core

public struct HomeModule {
    public static func makeHomeView(
        dependencies: HomeDependencies
    ) -> some View {
        HomeView(
            viewModel: HomeViewModel(
                productRepository: dependencies.productRepository,
                analytics: dependencies.analytics
            )
        )
    }
}

public protocol HomeDependencies {
    var productRepository: ProductRepository { get }
    var analytics: AnalyticsService { get }
}
```

## Feature Module Contents

```swift
// FeatureHome/Sources/Views/HomeView.swift
import SwiftUI

struct HomeView: View {
    @State private var viewModel: HomeViewModel
    
    init(viewModel: HomeViewModel) {
        _viewModel = State(initialValue: viewModel)
    }
    
    var body: some View {
        NavigationStack {
            ProductGrid(products: viewModel.products)
                .navigationTitle("Home")
        }
        .task {
            await viewModel.loadProducts()
        }
    }
}

// FeatureHome/Sources/ViewModels/HomeViewModel.swift
@Observable
@MainActor
final class HomeViewModel {
    private(set) var products: [Product] = []
    
    private let productRepository: ProductRepository
    private let analytics: AnalyticsService
    
    init(productRepository: ProductRepository, analytics: AnalyticsService) {
        self.productRepository = productRepository
        self.analytics = analytics
    }
    
    func loadProducts() async {
        products = (try? await productRepository.fetchFeatured()) ?? []
        analytics.track("home_loaded", properties: ["count": products.count])
    }
}
```

## Module Layers

```
┌─────────────────────────────────────────────┐
│                  App Layer                  │
│        (Composition, Navigation)            │
├─────────────────────────────────────────────┤
│              Feature Modules                │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐       │
│  │  Home   │ │ Profile │ │  Cart   │       │
│  └────┬────┘ └────┬────┘ └────┬────┘       │
├───────┼───────────┼───────────┼─────────────┤
│       │    Shared Modules     │             │
│  ┌────┴────┐ ┌────┴────┐ ┌────┴────┐       │
│  │ Domain  │ │  Core   │ │  UI/DS  │       │
│  └─────────┘ └─────────┘ └─────────┘       │
└─────────────────────────────────────────────┘
```

## App Composition

```swift
// App/Sources/AppDependencies.swift
import Core
import Domain
import FeatureHome
import FeatureProfile

final class AppDependencies: HomeDependencies, ProfileDependencies {
    lazy var apiClient: APIClient = URLSessionAPIClient(baseURL: Config.apiURL)
    lazy var productRepository: ProductRepository = APIProductRepository(client: apiClient)
    lazy var userRepository: UserRepository = APIUserRepository(client: apiClient)
    lazy var analytics: AnalyticsService = MixpanelAnalytics()
}

// App/Sources/AppView.swift
struct AppView: View {
    let dependencies = AppDependencies()
    
    var body: some View {
        TabView {
            HomeModule.makeHomeView(dependencies: dependencies)
                .tabItem { Label("Home", systemImage: "house") }
            
            ProfileModule.makeProfileView(dependencies: dependencies)
                .tabItem { Label("Profile", systemImage: "person") }
        }
    }
}
```

## Benefits

| Benefit | Description |
|---------|-------------|
| Parallel development | Teams work on separate modules |
| Faster builds | Incremental compilation |
| Clear boundaries | Enforced by module visibility |
| Testability | Isolated unit tests |
| Reusability | Share modules across apps |

## Related Patterns
- [[Swift-Package-Manager]] - Module management
- [[Module-Communication]] - Inter-module coordination
- [[Clean-Architecture]] - Layer separation

## Tags
#modularization #architecture #spm #scalability #organization
