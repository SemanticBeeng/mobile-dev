// MARK: - Contact Model
// Shared across all MVC example apps

import Foundation

/// Contact entity - pure data structure with business logic
public struct Contact: Identifiable, Equatable, Sendable {
    public let id: UUID
    public var firstName: String
    public var lastName: String
    public var email: String
    public var phone: String
    
    public init(
        id: UUID = UUID(),
        firstName: String,
        lastName: String,
        email: String,
        phone: String = ""
    ) {
        self.id = id
        self.firstName = firstName
        self.lastName = lastName
        self.email = email
        self.phone = phone
    }
}

// MARK: - Business Logic
extension Contact {
    /// Full display name
    public var fullName: String {
        "\(firstName) \(lastName)"
    }
    
    /// Validation: requires name and valid email
    public var isValid: Bool {
        !firstName.isEmpty && !lastName.isEmpty && email.contains("@")
    }
    
    /// Formatted phone for display
    public var formattedPhone: String {
        guard !phone.isEmpty else { return "No phone" }
        return phone
    }
}

// MARK: - Sample Data
extension Contact {
    public static let samples: [Contact] = [
        Contact(firstName: "John", lastName: "Appleseed", email: "john@apple.com", phone: "555-1234"),
        Contact(firstName: "Jane", lastName: "Doe", email: "jane@example.com", phone: "555-5678"),
        Contact(firstName: "Tim", lastName: "Cook", email: "tim@apple.com", phone: "555-0000"),
    ]
    
    public static let empty = Contact(firstName: "", lastName: "", email: "", phone: "")
}
