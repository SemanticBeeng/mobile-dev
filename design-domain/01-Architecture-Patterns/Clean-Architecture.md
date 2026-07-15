# Clean Architecture

> Uncle Bob's layered architecture adapted for iOS development.

## Overview
Clean Architecture enforces separation of concerns through dependency rules: inner layers know nothing about outer layers.

## Layer Structure

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│         (Views, ViewModels)             │
├─────────────────────────────────────────┤
│            Domain Layer                 │
│    (Use Cases, Entities, Interfaces)    │
├─────────────────────────────────────────┤
│             Data Layer                  │
│   (Repositories, Data Sources, DTOs)    │
└─────────────────────────────────────────┘
```

## Dependency Rule
Dependencies point inward. Domain layer has zero external dependencies.

## iOS Implementation

### Domain Layer
```swift
// Entity
struct Book: Identifiable {
    let id: UUID
    var title: String
    var author: String
}

// Repository Protocol (Port)
protocol BookRepository {
    func fetchAll() async throws -> [Book]
    func save(_ book: Book) async throws
}

// Use Case
struct FetchBooksUseCase {
    private let repository: BookRepository
    
    init(repository: BookRepository) {
        self.repository = repository
    }
    
    func execute() async throws -> [Book] {
        try await repository.fetchAll()
    }
}
```

### Data Layer
```swift
// DTO
struct BookDTO: Codable {
    let id: String
    let title: String
    let authorName: String
    
    func toDomain() -> Book {
        Book(id: UUID(uuidString: id)!, 
             title: title, 
             author: authorName)
    }
}

// Repository Implementation (Adapter)
final class BookRepositoryImpl: BookRepository {
    private let apiClient: APIClient
    private let cache: BookCache
    
    func fetchAll() async throws -> [Book] {
        let dtos = try await apiClient.get("/books")
        return dtos.map { $0.toDomain() }
    }
}
```

### Presentation Layer
```swift
@Observable
class BookListViewModel {
    private(set) var books: [Book] = []
    
    private let fetchBooksUseCase: FetchBooksUseCase
    
    func load() async {
        books = (try? await fetchBooksUseCase.execute()) ?? []
    }
}
```

## Pros & Cons

| Pros | Cons |
|------|------|
| Testable in isolation | Over-engineering for small apps |
| Framework independent | Many files/boilerplate |
| Clear boundaries | Learning curve |
| Flexible data sources | Mapping overhead |

## Related Patterns
- [[MVVM]] - Presentation layer pattern
- [[Repository-Pattern]] - Data layer abstraction
- [[DTO-Pattern]] - Data transfer between layers
- [[Protocol-Oriented-Dependencies]] - Dependency inversion

## Tags
#architecture #clean-architecture #solid #layers #uncle-bob
