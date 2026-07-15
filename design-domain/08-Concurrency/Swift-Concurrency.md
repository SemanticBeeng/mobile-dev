# Swift Concurrency

> Modern async/await patterns for iOS development.

## Overview
Swift's structured concurrency model (iOS 15+) provides safe, readable asynchronous code with compile-time data race protection.

## Core Concepts

### async/await

```swift
// Async function
func fetchUser(id: UUID) async throws -> User {
    let url = URL(string: "https://api.example.com/users/\(id)")!
    let (data, _) = try await URLSession.shared.data(from: url)
    return try JSONDecoder().decode(User.self, from: data)
}

// Calling async code
func loadUserProfile() async {
    do {
        let user = try await fetchUser(id: currentUserId)
        // Update UI
    } catch {
        // Handle error
    }
}
```

### Task - Entry Point from Sync Code

```swift
struct UserView: View {
    @State private var user: User?
    
    var body: some View {
        Group {
            if let user {
                Text(user.name)
            } else {
                ProgressView()
            }
        }
        .task {  // Automatically cancels when view disappears
            user = try? await fetchUser(id: userId)
        }
    }
}

// Manual task creation
Button("Load") {
    Task {
        await viewModel.loadData()
    }
}
```

### Parallel Execution

```swift
// Sequential (slow)
let user = try await fetchUser(id: userId)
let posts = try await fetchPosts(userId: userId)
let followers = try await fetchFollowers(userId: userId)

// Parallel with async let (fast)
async let user = fetchUser(id: userId)
async let posts = fetchPosts(userId: userId)  
async let followers = fetchFollowers(userId: userId)

let profile = try await Profile(
    user: user,
    posts: posts,
    followers: followers
)
```

### TaskGroup

```swift
func fetchAllImages(urls: [URL]) async throws -> [UIImage] {
    try await withThrowingTaskGroup(of: UIImage.self) { group in
        for url in urls {
            group.addTask {
                let (data, _) = try await URLSession.shared.data(from: url)
                guard let image = UIImage(data: data) else {
                    throw ImageError.invalidData
                }
                return image
            }
        }
        
        var images: [UIImage] = []
        for try await image in group {
            images.append(image)
        }
        return images
    }
}
```

## Cancellation

```swift
func fetchData() async throws -> Data {
    let url = URL(string: "https://example.com/large-file")!
    
    // Check cancellation
    try Task.checkCancellation()
    
    let (data, _) = try await URLSession.shared.data(from: url)
    
    // Or check without throwing
    if Task.isCancelled {
        return Data()
    }
    
    return data
}

// Cancel a task
let task = Task {
    await longRunningOperation()
}

task.cancel()  // Cooperative cancellation
```

## Continuations - Bridging Callback APIs

```swift
func fetchUserLegacy(completion: @escaping (Result<User, Error>) -> Void) {
    // Old callback-based API
}

func fetchUser() async throws -> User {
    try await withCheckedThrowingContinuation { continuation in
        fetchUserLegacy { result in
            switch result {
            case .success(let user):
                continuation.resume(returning: user)
            case .failure(let error):
                continuation.resume(throwing: error)
            }
        }
    }
}
```

## AsyncSequence

```swift
// Built-in async sequences
for try await line in url.lines {
    process(line)
}

// Custom AsyncStream
func notifications() -> AsyncStream<Notification> {
    AsyncStream { continuation in
        let observer = NotificationCenter.default.addObserver(
            forName: .myNotification,
            object: nil,
            queue: .main
        ) { notification in
            continuation.yield(notification)
        }
        
        continuation.onTermination = { _ in
            NotificationCenter.default.removeObserver(observer)
        }
    }
}

// Usage
for await notification in notifications() {
    handleNotification(notification)
}
```

## Error Handling Patterns

```swift
enum FetchError: Error {
    case networkError(underlying: Error)
    case decodingError
    case notFound
}

func fetchWithRetry<T>(
    maxAttempts: Int = 3,
    operation: () async throws -> T
) async throws -> T {
    var lastError: Error?
    
    for attempt in 1...maxAttempts {
        do {
            return try await operation()
        } catch {
            lastError = error
            if attempt < maxAttempts {
                try await Task.sleep(for: .seconds(pow(2.0, Double(attempt))))
            }
        }
    }
    
    throw lastError!
}
```

## Related Patterns
- [[Actors]] - Data race protection
- [[MainActor]] - UI thread safety
- [[Structured-Concurrency]] - Task hierarchies

## Tags
#concurrency #async #await #swift #modern
