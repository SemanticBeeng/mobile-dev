# Network Layer Design

> Building a robust, testable API client architecture.

## Overview
A well-designed network layer abstracts HTTP concerns, handles errors gracefully, and enables easy testing.

## Layer Structure

```
┌─────────────────────────────────────┐
│          Feature Layer              │
│        (Uses Repository)            │
├─────────────────────────────────────┤
│          Repository Layer           │
│     (Calls API Client, Maps DTOs)   │
├─────────────────────────────────────┤
│          API Client Layer           │
│    (HTTP, Auth, Error Handling)     │
├─────────────────────────────────────┤
│          Network Session            │
│         (URLSession)                │
└─────────────────────────────────────┘
```

## API Client Protocol

```swift
protocol APIClient {
    func request<T: Decodable>(_ endpoint: Endpoint) async throws -> T
}

struct Endpoint {
    let path: String
    let method: HTTPMethod
    let headers: [String: String]
    let queryItems: [URLQueryItem]
    let body: Encodable?
    
    static func get(_ path: String, query: [String: String] = [:]) -> Endpoint {
        Endpoint(
            path: path,
            method: .get,
            headers: [:],
            queryItems: query.map { URLQueryItem(name: $0.key, value: $0.value) },
            body: nil
        )
    }
    
    static func post<T: Encodable>(_ path: String, body: T) -> Endpoint {
        Endpoint(path: path, method: .post, headers: [:], queryItems: [], body: body)
    }
}

enum HTTPMethod: String {
    case get = "GET"
    case post = "POST"
    case put = "PUT"
    case patch = "PATCH"
    case delete = "DELETE"
}
```

## Implementation

```swift
final class URLSessionAPIClient: APIClient {
    private let baseURL: URL
    private let session: URLSession
    private let decoder: JSONDecoder
    private let encoder: JSONEncoder
    private let authProvider: AuthProvider
    
    init(
        baseURL: URL,
        session: URLSession = .shared,
        authProvider: AuthProvider
    ) {
        self.baseURL = baseURL
        self.session = session
        self.authProvider = authProvider
        
        self.decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        decoder.dateDecodingStrategy = .iso8601
        
        self.encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
    }
    
    func request<T: Decodable>(_ endpoint: Endpoint) async throws -> T {
        var request = try buildRequest(endpoint)
        
        // Add auth header
        if let token = await authProvider.accessToken {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NetworkError.invalidResponse
        }
        
        try validateResponse(httpResponse, data: data)
        
        return try decoder.decode(T.self, from: data)
    }
    
    private func buildRequest(_ endpoint: Endpoint) throws -> URLRequest {
        var components = URLComponents(url: baseURL.appendingPathComponent(endpoint.path), resolvingAgainstBaseURL: true)!
        components.queryItems = endpoint.queryItems.isEmpty ? nil : endpoint.queryItems
        
        var request = URLRequest(url: components.url!)
        request.httpMethod = endpoint.method.rawValue
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if let body = endpoint.body {
            request.httpBody = try encoder.encode(AnyEncodable(body))
        }
        
        return request
    }
    
    private func validateResponse(_ response: HTTPURLResponse, data: Data) throws {
        switch response.statusCode {
        case 200...299:
            return
        case 401:
            throw NetworkError.unauthorized
        case 404:
            throw NetworkError.notFound
        case 500...599:
            throw NetworkError.serverError(response.statusCode)
        default:
            let apiError = try? decoder.decode(APIErrorDTO.self, from: data)
            throw NetworkError.apiError(apiError?.message ?? "Unknown error")
        }
    }
}
```

## Error Handling

```swift
enum NetworkError: LocalizedError {
    case invalidResponse
    case unauthorized
    case notFound
    case serverError(Int)
    case apiError(String)
    case noConnection
    case timeout
    
    var errorDescription: String? {
        switch self {
        case .unauthorized: return "Please log in again"
        case .notFound: return "Resource not found"
        case .serverError(let code): return "Server error (\(code))"
        case .apiError(let message): return message
        case .noConnection: return "No internet connection"
        case .timeout: return "Request timed out"
        default: return "An error occurred"
        }
    }
}
```

## Retry & Refresh Token

```swift
final class RetryingAPIClient: APIClient {
    private let client: APIClient
    private let authService: AuthService
    private let maxRetries = 3
    
    func request<T: Decodable>(_ endpoint: Endpoint) async throws -> T {
        var lastError: Error?
        
        for attempt in 0..<maxRetries {
            do {
                return try await client.request(endpoint)
            } catch NetworkError.unauthorized {
                try await authService.refreshToken()
                continue
            } catch {
                lastError = error
                if !isRetryable(error) { break }
                try await Task.sleep(nanoseconds: UInt64(pow(2.0, Double(attempt))) * 1_000_000_000)
            }
        }
        
        throw lastError ?? NetworkError.invalidResponse
    }
}
```

## Testing

```swift
class MockAPIClient: APIClient {
    var stubbedResponse: Any?
    var stubbedError: Error?
    var capturedEndpoints: [Endpoint] = []
    
    func request<T: Decodable>(_ endpoint: Endpoint) async throws -> T {
        capturedEndpoints.append(endpoint)
        
        if let error = stubbedError {
            throw error
        }
        
        return stubbedResponse as! T
    }
}
```

## Related Patterns
- [[Repository-Pattern]] - Uses API client
- [[DTO-Pattern]] - Response models
- [[Swift-Concurrency]] - Async/await

## Tags
#data-layer #networking #api #urlsession #async
