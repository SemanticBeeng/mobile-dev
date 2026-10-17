# DTO Pattern - Data Transfer Objects

> Separating external data representations from domain models.

## Overview
DTOs are simple data structures for transferring data between layers or systems, decoupling API contracts from domain logic.

## Why DTOs?

- **Isolation**: API changes don't break domain models
- **Mapping**: Transform snake_case to camelCase, flatten nested data
- **Validation**: Catch malformed data at the boundary
- **Versioning**: Support multiple API versions

## Basic Implementation

```swift
// Domain Model (what your app uses)
struct User: Identifiable {
    let id: UUID
    var name: String
    var email: Email
    var createdAt: Date
    var isVerified: Bool
}

// DTO (what the API returns)
struct UserDTO: Codable {
    let id: String
    let first_name: String
    let last_name: String
    let email_address: String
    let created_at: String
    let verified: Int  // API returns 0 or 1
    
    // Transform to domain
    func toDomain() throws -> User {
        guard let uuid = UUID(uuidString: id) else {
            throw MappingError.invalidId
        }
        guard let email = Email(rawValue: email_address) else {
            throw MappingError.invalidEmail
        }
        guard let date = ISO8601DateFormatter().date(from: created_at) else {
            throw MappingError.invalidDate
        }
        
        return User(
            id: uuid,
            name: "\(first_name) \(last_name)",
            email: email,
            createdAt: date,
            isVerified: verified == 1
        )
    }
}

// Reverse mapping for sending data
extension UserDTO {
    init(from user: User) {
        let names = user.name.split(separator: " ")
        self.id = user.id.uuidString
        self.first_name = String(names.first ?? "")
        self.last_name = String(names.dropFirst().joined(separator: " "))
        self.email_address = user.email.rawValue
        self.created_at = ISO8601DateFormatter().string(from: user.createdAt)
        self.verified = user.isVerified ? 1 : 0
    }
}
```

## Nested DTOs

```swift
struct OrderDTO: Codable {
    let order_id: String
    let items: [OrderItemDTO]
    let shipping_address: AddressDTO
    let payment_info: PaymentDTO?
    
    func toDomain() throws -> Order {
        Order(
            id: UUID(uuidString: order_id)!,
            items: try items.map { try $0.toDomain() },
            shippingAddress: try shipping_address.toDomain(),
            payment: try payment_info?.toDomain()
        )
    }
}
```

## Request DTOs

```swift
struct CreateUserRequestDTO: Encodable {
    let first_name: String
    let last_name: String
    let email: String
    let password: String
}

struct UpdateUserRequestDTO: Encodable {
    let first_name: String?
    let last_name: String?
    let email: String?
}
```

## API Response Wrapper

```swift
struct APIResponse<T: Decodable>: Decodable {
    let success: Bool
    let data: T?
    let error: APIErrorDTO?
    let pagination: PaginationDTO?
}

struct PaginationDTO: Decodable {
    let page: Int
    let per_page: Int
    let total: Int
    let total_pages: Int
}

// Usage
let response: APIResponse<[UserDTO]> = try await apiClient.get("/users")
if response.success, let dtos = response.data {
    let users = try dtos.map { try $0.toDomain() }
}
```

## DTO Layer Architecture

```
┌──────────────────────────────────────┐
│          Presentation Layer          │
│           (Domain Models)            │
├──────────────────────────────────────┤
│           Repository Layer           │
│     DTO ↔ Domain Mapping Here        │
├──────────────────────────────────────┤
│            Network Layer             │
│              (DTOs Only)             │
└──────────────────────────────────────┘
```

## Best Practices

1. **DTOs are dumb** - no business logic
2. **Map at boundaries** - repository/service layer
3. **Fail explicitly** - throw on invalid data
4. **Version DTOs** - `UserDTOv2` if needed
5. **Test mappings** - critical transformation code

## Related Patterns
- [[Repository-Pattern]] - Performs DTO mapping
- [[Network-Layer-Design]] - Uses DTOs
- [[Clean-Architecture]] - DTOs at boundaries

## Tags
#data-layer #dto #mapping #api #codable
