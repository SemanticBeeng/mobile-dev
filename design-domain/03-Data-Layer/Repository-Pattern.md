# Repository Pattern

> Abstracting data sources behind a clean interface.

## Overview
The Repository Pattern provides a collection-like interface for accessing domain objects, hiding the complexity of data sources.

## Purpose
- Abstract data source implementation
- Enable offline-first architecture  
- Simplify testing with mock repositories
- Allow swapping data sources

## Basic Implementation

```swift
// Domain Entity
struct Product: Identifiable {
    let id: UUID
    var name: String
    var price: Decimal
    var stock: Int
}

// Repository Protocol
protocol ProductRepository {
    func getAll() async throws -> [Product]
    func getById(_ id: UUID) async throws -> Product?
    func save(_ product: Product) async throws
    func delete(_ id: UUID) async throws
}

// Remote Implementation
final class RemoteProductRepository: ProductRepository {
    private let apiClient: APIClient
    
    init(apiClient: APIClient) {
        self.apiClient = apiClient
    }
    
    func getAll() async throws -> [Product] {
        let dtos: [ProductDTO] = try await apiClient.get("/products")
        return dtos.map { $0.toDomain() }
    }
    
    func getById(_ id: UUID) async throws -> Product? {
        let dto: ProductDTO = try await apiClient.get("/products/\(id)")
        return dto.toDomain()
    }
    
    func save(_ product: Product) async throws {
        let dto = ProductDTO(from: product)
        try await apiClient.post("/products", body: dto)
    }
    
    func delete(_ id: UUID) async throws {
        try await apiClient.delete("/products/\(id)")
    }
}
```

## Caching Repository

```swift
final class CachingProductRepository: ProductRepository {
    private let remote: ProductRepository
    private let cache: ProductCache
    
    init(remote: ProductRepository, cache: ProductCache) {
        self.remote = remote
        self.cache = cache
    }
    
    func getAll() async throws -> [Product] {
        // Try cache first
        if let cached = await cache.getAll(), !cached.isEmpty {
            // Refresh in background
            Task {
                if let fresh = try? await remote.getAll() {
                    await cache.save(fresh)
                }
            }
            return cached
        }
        
        // Fetch from remote
        let products = try await remote.getAll()
        await cache.save(products)
        return products
    }
}
```

## Offline-First Repository

```swift
final class OfflineFirstProductRepository: ProductRepository {
    private let remote: ProductRepository
    private let local: ProductRepository
    private let networkMonitor: NetworkMonitor
    
    func save(_ product: Product) async throws {
        // Always save locally first
        try await local.save(product)
        
        if networkMonitor.isConnected {
            try await remote.save(product)
        } else {
            // Queue for sync
            await SyncQueue.shared.enqueue(.save(product))
        }
    }
}
```

## Usage in ViewModel

```swift
@Observable
class ProductListViewModel {
    private(set) var products: [Product] = []
    private(set) var error: Error?
    
    private let repository: ProductRepository
    
    init(repository: ProductRepository) {
        self.repository = repository
    }
    
    func loadProducts() async {
        do {
            products = try await repository.getAll()
        } catch {
            self.error = error
        }
    }
}
```

## Testing

```swift
class MockProductRepository: ProductRepository {
    var stubbedProducts: [Product] = []
    var savedProducts: [Product] = []
    
    func getAll() async throws -> [Product] {
        stubbedProducts
    }
    
    func save(_ product: Product) async throws {
        savedProducts.append(product)
    }
}

@Test func testLoadProducts() async {
    let mock = MockProductRepository()
    mock.stubbedProducts = [Product.mock]
    
    let viewModel = ProductListViewModel(repository: mock)
    await viewModel.loadProducts()
    
    #expect(viewModel.products.count == 1)
}
```

## Related Patterns
- [[Clean-Architecture]] - Repository in data layer
- [[DTO-Pattern]] - Data transformation
- [[Protocol-Oriented-Dependencies]] - Abstraction

## Tags
#data-layer #repository #abstraction #testing #offline
