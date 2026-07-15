// MARK: - MODEL
// The Model represents data and business logic.
// It knows nothing about how it will be displayed.
//
// NOTE: For Swift Package projects, use SharedModels package instead.
// This file exists for standalone Xcode project compatibility.

import Foundation

/// Contact entity - pure data structure
struct Contact: Identifiable, Equatable {
    let id: UUID
    var firstName: String
    var lastName: String
    var email: String
    var phone: String
    
    init(
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

// MARK: - Model Business Logic
extension Contact {
    /// Computed property - presentation logic can live here
    var fullName: String {
        "\(firstName) \(lastName)"
    }
    
    /// Validation logic belongs in the Model
    var isValid: Bool {
        !firstName.isEmpty && !lastName.isEmpty && email.contains("@")
    }
    
    /// Business rule: formatted phone
    var formattedPhone: String {
        guard !phone.isEmpty else { return "No phone" }
        return phone
    }
}

// MARK: - Sample Data (for development/testing)
extension Contact {
    static let samples: [Contact] = [
        Contact(firstName: "John", lastName: "Appleseed", email: "john@apple.com", phone: "555-1234"),
        Contact(firstName: "Jane", lastName: "Doe", email: "jane@example.com", phone: "555-5678"),
        Contact(firstName: "Tim", lastName: "Cook", email: "tim@apple.com", phone: "555-0000"),
    ]
    
    static let empty = Contact(firstName: "", lastName: "", email: "", phone: "")
}
