# SwiftData Architecture

> Apple's modern persistence framework (iOS 17+).

## Overview
SwiftData is a declarative, Swift-native persistence framework built on Core Data, designed for SwiftUI integration.

## Core Components

### @Model
Macro that transforms a class into a persistent model.

```swift
import SwiftData

@Model
final class Book {
    var title: String
    var author: String
    var publishDate: Date
    var pageCount: Int
    var isRead: Bool
    
    @Relationship(deleteRule: .cascade)
    var chapters: [Chapter]
    
    init(title: String, author: String, publishDate: Date, pageCount: Int) {
        self.title = title
        self.author = author
        self.publishDate = publishDate
        self.pageCount = pageCount
        self.isRead = false
        self.chapters = []
    }
}

@Model
final class Chapter {
    var title: String
    var pageNumber: Int
    var book: Book?
    
    init(title: String, pageNumber: Int) {
        self.title = title
        self.pageNumber = pageNumber
    }
}
```

### ModelContainer
Manages the database schema and storage.

```swift
@main
struct BookApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .modelContainer(for: [Book.self, Chapter.self])
    }
}
```

### ModelContext
Tracks changes and saves data.

```swift
struct BookListView: View {
    @Environment(\.modelContext) private var context
    @Query var books: [Book]
    
    func addBook() {
        let book = Book(title: "New Book", author: "Author", publishDate: .now, pageCount: 200)
        context.insert(book)
    }
    
    func deleteBooks(at offsets: IndexSet) {
        for index in offsets {
            context.delete(books[index])
        }
    }
}
```

## @Query - Fetching Data

```swift
// Basic query
@Query var books: [Book]

// Sorted
@Query(sort: \Book.title) var books: [Book]

// Filtered
@Query(filter: #Predicate<Book> { $0.isRead == false })
var unreadBooks: [Book]

// Complex query
@Query(
    filter: #Predicate<Book> { book in
        book.pageCount > 200 && book.author.contains("Swift")
    },
    sort: [SortDescriptor(\Book.publishDate, order: .reverse)],
    animation: .default
)
var filteredBooks: [Book]
```

## Dynamic Queries

```swift
struct BookListView: View {
    @State private var searchText = ""
    @State private var sortOrder = SortDescriptor(\Book.title)
    
    var body: some View {
        BookList(searchText: searchText, sortOrder: sortOrder)
    }
}

struct BookList: View {
    @Query private var books: [Book]
    
    init(searchText: String, sortOrder: SortDescriptor<Book>) {
        let predicate = #Predicate<Book> { book in
            searchText.isEmpty || book.title.localizedStandardContains(searchText)
        }
        _books = Query(filter: predicate, sort: [sortOrder])
    }
    
    var body: some View {
        List(books) { book in
            BookRow(book: book)
        }
    }
}
```

## Configuration

```swift
let schema = Schema([Book.self, Chapter.self])
let config = ModelConfiguration(
    schema: schema,
    isStoredInMemoryOnly: false,
    cloudKitDatabase: .automatic
)
let container = try ModelContainer(for: schema, configurations: config)
```

## Repository with SwiftData

```swift
protocol BookRepository {
    func fetchAll() async -> [Book]
    func save(_ book: Book) async
    func delete(_ book: Book) async
}

@MainActor
final class SwiftDataBookRepository: BookRepository {
    private let context: ModelContext
    
    init(context: ModelContext) {
        self.context = context
    }
    
    func fetchAll() async -> [Book] {
        let descriptor = FetchDescriptor<Book>(sortBy: [SortDescriptor(\.title)])
        return (try? context.fetch(descriptor)) ?? []
    }
    
    func save(_ book: Book) async {
        context.insert(book)
        try? context.save()
    }
    
    func delete(_ book: Book) async {
        context.delete(book)
        try? context.save()
    }
}
```

## Migration

```swift
enum BookSchemaV1: VersionedSchema {
    static var versionIdentifier = Schema.Version(1, 0, 0)
    static var models: [any PersistentModel.Type] { [Book.self] }
}

enum BookSchemaV2: VersionedSchema {
    static var versionIdentifier = Schema.Version(2, 0, 0)
    static var models: [any PersistentModel.Type] { [Book.self, Chapter.self] }
}

enum BookMigrationPlan: SchemaMigrationPlan {
    static var schemas: [any VersionedSchema.Type] {
        [BookSchemaV1.self, BookSchemaV2.self]
    }
    
    static var stages: [MigrationStage] {
        [migrateV1toV2]
    }
    
    static let migrateV1toV2 = MigrationStage.lightweight(
        fromVersion: BookSchemaV1.self,
        toVersion: BookSchemaV2.self
    )
}
```

## Related Patterns
- [[Repository-Pattern]] - Abstraction layer
- [[Core-Data-Architecture]] - Underlying framework
- [[MVVM]] - Integration pattern

## Tags
#persistence #swiftdata #ios17 #database #apple
