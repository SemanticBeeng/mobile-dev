# Protocol-Oriented Dependencies

> Abstracting dependencies using protocols for testability and flexibility.

## Overview
Protocol-oriented programming enables dependency inversion, allowing code to depend on abstractions rather than concrete implementations.

## The Problem

```swift
// ❌ Tight coupling - hard to test
class UserViewModel {
    func loadUser() async {
        let user = await URLSession.shared.data(from: userURL)  // Direct dependency
        await CoreDataStack.shared.save(user)  // Magic singleton
    }
}
```

## The Solution

```swift
// ✅ Protocol abstractions
protocol UserFetching {
    func fetchUser(id: UUID) async throws -> User
}

protocol UserPersisting {
    func save(_ user: User) async throws
    func load(id: UUID) async throws -> User?
}

class UserViewModel {
    private let fetcher: UserFetching
    private let persistence: UserPersisting
    
    init(fetcher: UserFetching, persistence: UserPersisting) {
        self.fetcher = fetcher
        self.persistence = persistence
    }
    
    func loadUser(id: UUID) async throws {
        let user = try await fetcher.fetchUser(id: id)
        try await persistence.save(user)
    }
}
```

## Production Implementations

```swift
struct APIUserFetcher: UserFetching {
    private let apiClient: APIClient
    
    func fetchUser(id: UUID) async throws -> User {
        try await apiClient.request(.get("/users/\(id)"))
    }
}

struct CoreDataUserPersistence: UserPersisting {
    private let context: NSManagedObjectContext
    
    func save(_ user: User) async throws {
        // Core Data save logic
    }
    
    func load(id: UUID) async throws -> User? {
        // Core Data fetch logic
    }
}
```

## Mock Implementations

```swift
class MockUserFetcher: UserFetching {
    var stubbedUser: User?
    var fetchError: Error?
    var fetchCallCount = 0
    
    func fetchUser(id: UUID) async throws -> User {
        fetchCallCount += 1
        if let error = fetchError {
            throw error
        }
        return stubbedUser ?? User.mock
    }
}

class MockUserPersistence: UserPersisting {
    var savedUsers: [User] = []
    var storedUsers: [UUID: User] = [:]
    
    func save(_ user: User) async throws {
        savedUsers.append(user)
        storedUsers[user.id] = user
    }
    
    func load(id: UUID) async throws -> User? {
        storedUsers[id]
    }
}
```

## Testing

```swift
@Test func testLoadUserSavesToPersistence() async throws {
    // Arrange
    let mockFetcher = MockUserFetcher()
    let mockPersistence = MockUserPersistence()
    let viewModel = UserViewModel(fetcher: mockFetcher, persistence: mockPersistence)
    
    let expectedUser = User(id: UUID(), name: "Test")
    mockFetcher.stubbedUser = expectedUser
    
    // Act
    try await viewModel.loadUser(id: expectedUser.id)
    
    // Assert
    #expect(mockPersistence.savedUsers.count == 1)
    #expect(mockPersistence.savedUsers.first?.id == expectedUser.id)
}
```

## Protocol Composition

```swift
protocol UserRepository: UserFetching, UserPersisting {
    func sync() async throws
}

// Or using typealias
typealias UserDataSource = UserFetching & UserPersisting
```

## Default Implementations

```swift
protocol Cacheable {
    associatedtype Item
    func cache(_ item: Item, forKey key: String)
    func retrieve(forKey key: String) -> Item?
}

extension Cacheable {
    func cacheIfAbsent(_ item: Item, forKey key: String) {
        if retrieve(forKey: key) == nil {
            cache(item, forKey: key)
        }
    }
}
```

## Associated Types Pattern

```swift
protocol Repository {
    associatedtype Entity: Identifiable
    
    func fetchAll() async throws -> [Entity]
    func fetch(id: Entity.ID) async throws -> Entity?
    func save(_ entity: Entity) async throws
    func delete(id: Entity.ID) async throws
}

struct ProductRepository: Repository {
    typealias Entity = Product
    
    func fetchAll() async throws -> [Product] { ... }
    func fetch(id: UUID) async throws -> Product? { ... }
    func save(_ entity: Product) async throws { ... }
    func delete(id: UUID) async throws { ... }
}
```

## Related Patterns
- [[Constructor-Injection]] - Passing protocol dependencies
- [[Environment-Injection]] - SwiftUI protocol injection
- [[Clean-Architecture]] - Protocol boundaries

## Tags
#dependency-injection #protocols #testing #abstraction #solid
