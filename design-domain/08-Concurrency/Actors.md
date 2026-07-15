# Actors

> Protecting shared mutable state from data races.

## Overview
Actors are reference types that protect their mutable state by serializing access, ensuring only one task accesses the state at a time.

## Basic Actor

```swift
actor BankAccount {
    private(set) var balance: Decimal
    
    init(initialBalance: Decimal) {
        self.balance = initialBalance
    }
    
    func deposit(_ amount: Decimal) {
        balance += amount
    }
    
    func withdraw(_ amount: Decimal) throws {
        guard balance >= amount else {
            throw BankError.insufficientFunds
        }
        balance -= amount
    }
    
    func transfer(amount: Decimal, to other: BankAccount) async throws {
        try withdraw(amount)
        await other.deposit(amount)
    }
}

// Usage - all calls are async from outside
let account = BankAccount(initialBalance: 100)
await account.deposit(50)
print(await account.balance)  // 150
```

## Actor Isolation

```swift
actor DataStore {
    private var cache: [String: Data] = [:]
    
    // Isolated - can only be called with await
    func store(_ data: Data, forKey key: String) {
        cache[key] = data
    }
    
    func retrieve(forKey key: String) -> Data? {
        cache[key]
    }
    
    // nonisolated - can be called synchronously
    nonisolated var storeName: String {
        "MainDataStore"  // Must not access mutable state
    }
    
    nonisolated func description() -> String {
        "A data store"
    }
}
```

## Actor Reentrancy

```swift
actor ImageLoader {
    private var cache: [URL: UIImage] = [:]
    
    func loadImage(from url: URL) async throws -> UIImage {
        // Check cache
        if let cached = cache[url] {
            return cached
        }
        
        // ⚠️ Suspension point - other tasks can run
        let (data, _) = try await URLSession.shared.data(from: url)
        
        // After suspension, state might have changed!
        // Another task might have cached this URL
        if let cached = cache[url] {
            return cached  // Use cached version
        }
        
        guard let image = UIImage(data: data) else {
            throw ImageError.invalid
        }
        
        cache[url] = image
        return image
    }
}
```

## Global Actors

```swift
// Define a custom global actor
@globalActor
actor DatabaseActor {
    static let shared = DatabaseActor()
}

// Use global actor to isolate code
@DatabaseActor
class DatabaseManager {
    private var connection: Connection?
    
    func query(_ sql: String) -> [Row] {
        // All methods run on DatabaseActor
    }
}

// Isolate individual functions
@DatabaseActor
func saveToDB(_ data: Data) async {
    // Runs on DatabaseActor
}
```

## Sendable

```swift
// Sendable - safe to pass between concurrency domains

// Value types are implicitly Sendable
struct Point: Sendable {
    let x: Double
    let y: Double
}

// Classes must be carefully designed
final class Counter: Sendable {
    // Must be immutable or use locks
    private let lock = NSLock()
    private var _count = 0
    
    var count: Int {
        lock.lock()
        defer { lock.unlock() }
        return _count
    }
}

// Actors are always Sendable
actor SafeCounter: Sendable {
    private var count = 0
    
    func increment() {
        count += 1
    }
}

// Mark as @unchecked when you manage safety yourself
final class UnsafeButCareful: @unchecked Sendable {
    private let queue = DispatchQueue(label: "safe")
    private var data: [String] = []
    
    func append(_ item: String) {
        queue.sync { data.append(item) }
    }
}
```

## Actor vs Class

| Actor | Class |
|-------|-------|
| Thread-safe by default | Manual synchronization |
| Async access required | Sync access |
| No inheritance | Full inheritance |
| Serialized access | Concurrent access |

## When to Use Actors

✅ **Use actors for:**
- Shared mutable state accessed from multiple tasks
- Caches, managers, coordinators
- Replacing manual locking

❌ **Avoid actors for:**
- Pure data transfer (use structs)
- UI components (use @MainActor)
- Immutable data

## Related Patterns
- [[MainActor]] - UI thread actor
- [[Swift-Concurrency]] - async/await
- [[Structured-Concurrency]] - Task management

## Tags
#concurrency #actors #thread-safety #isolation #sendable
