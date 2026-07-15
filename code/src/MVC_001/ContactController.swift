// MARK: - CONTROLLER
// The Controller mediates between Model and View.
// It owns the data, handles user actions, and updates the view.

import Foundation
import Observation

/// ContactController manages the contact list state and operations.
/// In traditional MVC, this would be a UIViewController.
/// In SwiftUI, we use @Observable to notify views of changes.
@Observable
final class ContactController {
    
    // MARK: - State (owned by Controller)
    
    /// The list of contacts - Controller owns this data
    private(set) var contacts: [Contact] = []
    
    /// Currently selected contact for editing
    var selectedContact: Contact?
    
    /// Controls sheet presentation
    var isAddingContact = false
    
    /// Error state
    var errorMessage: String?
    
    // MARK: - Dependencies
    
    /// In a real app, this would be injected
    private let storage: ContactStorage
    
    // MARK: - Initialization
    
    init(storage: ContactStorage = InMemoryContactStorage()) {
        self.storage = storage
        loadContacts()
    }
    
    // MARK: - Controller Actions
    // These methods respond to user actions from the View
    
    /// Load contacts from storage
    func loadContacts() {
        contacts = storage.fetchAll()
    }
    
    /// Add a new contact
    func addContact(_ contact: Contact) {
        guard contact.isValid else {
            errorMessage = "Please fill in all required fields"
            return
        }
        
        storage.save(contact)
        contacts.append(contact)
        isAddingContact = false
        errorMessage = nil
    }
    
    /// Delete contacts at specified indices
    func deleteContacts(at offsets: IndexSet) {
        for index in offsets {
            let contact = contacts[index]
            storage.delete(contact.id)
        }
        contacts.remove(atOffsets: offsets)
    }
    
    /// Update an existing contact
    func updateContact(_ contact: Contact) {
        guard contact.isValid else {
            errorMessage = "Please fill in all required fields"
            return
        }
        
        storage.save(contact)
        if let index = contacts.firstIndex(where: { $0.id == contact.id }) {
            contacts[index] = contact
        }
        selectedContact = nil
        errorMessage = nil
    }
    
    /// Begin adding a new contact
    func beginAddingContact() {
        isAddingContact = true
    }
    
    /// Select a contact for editing
    func selectContact(_ contact: Contact) {
        selectedContact = contact
    }
    
    /// Clear any error
    func clearError() {
        errorMessage = nil
    }
}

// MARK: - Storage Protocol (Abstraction)

/// Protocol for contact persistence
protocol ContactStorage {
    func fetchAll() -> [Contact]
    func save(_ contact: Contact)
    func delete(_ id: UUID)
}

/// Simple in-memory storage for demonstration
final class InMemoryContactStorage: ContactStorage {
    private var contacts: [UUID: Contact] = [:]
    
    init() {
        // Pre-populate with sample data
        for contact in Contact.samples {
            contacts[contact.id] = contact
        }
    }
    
    func fetchAll() -> [Contact] {
        Array(contacts.values).sorted { $0.lastName < $1.lastName }
    }
    
    func save(_ contact: Contact) {
        contacts[contact.id] = contact
    }
    
    func delete(_ id: UUID) {
        contacts.removeValue(forKey: id)
    }
}
