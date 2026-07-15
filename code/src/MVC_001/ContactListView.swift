// MARK: - VIEW
// The View is responsible for displaying data and capturing user input.
// It should be as "dumb" as possible - no business logic!

import SwiftUI

/// Main view displaying the contact list
struct ContactListView: View {
    
    // The Controller - View doesn't own it, just references it
    @State private var controller = ContactController()
    
    var body: some View {
        NavigationStack {
            // View reads data FROM controller
            List {
                ForEach(controller.contacts) { contact in
                    ContactRow(contact: contact)
                        .onTapGesture {
                            // User action -> delegate to Controller
                            controller.selectContact(contact)
                        }
                }
                .onDelete { offsets in
                    // User action -> delegate to Controller
                    controller.deleteContacts(at: offsets)
                }
            }
            .navigationTitle("Contacts")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        // User action -> delegate to Controller
                        controller.beginAddingContact()
                    } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            // Controller state drives presentation
            .sheet(isPresented: $controller.isAddingContact) {
                ContactFormView(
                    contact: .empty,
                    onSave: { newContact in
                        controller.addContact(newContact)
                    },
                    onCancel: {
                        controller.isAddingContact = false
                    }
                )
            }
            .sheet(item: $controller.selectedContact) { contact in
                ContactFormView(
                    contact: contact,
                    onSave: { updated in
                        controller.updateContact(updated)
                    },
                    onCancel: {
                        controller.selectedContact = nil
                    }
                )
            }
            .alert("Error", isPresented: .init(
                get: { controller.errorMessage != nil },
                set: { if !$0 { controller.clearError() } }
            )) {
                Button("OK") { controller.clearError() }
            } message: {
                Text(controller.errorMessage ?? "")
            }
        }
    }
}

// MARK: - ContactRow (Sub-View)

/// A row displaying a single contact
struct ContactRow: View {
    let contact: Contact
    
    var body: some View {
        HStack {
            // Avatar placeholder
            Circle()
                .fill(Color.blue.opacity(0.2))
                .frame(width: 44, height: 44)
                .overlay {
                    Text(contact.firstName.prefix(1))
                        .font(.headline)
                        .foregroundStyle(.blue)
                }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(contact.fullName)
                    .font(.headline)
                Text(contact.email)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            
            Spacer()
            
            Image(systemName: "chevron.right")
                .foregroundStyle(.tertiary)
        }
        .padding(.vertical, 4)
    }
}

// MARK: - ContactFormView (Sub-View)

/// Form for adding/editing a contact
struct ContactFormView: View {
    @State private var contact: Contact
    let onSave: (Contact) -> Void
    let onCancel: () -> Void
    
    init(contact: Contact, onSave: @escaping (Contact) -> Void, onCancel: @escaping () -> Void) {
        _contact = State(initialValue: contact)
        self.onSave = onSave
        self.onCancel = onCancel
    }
    
    private var isEditing: Bool {
        contact.id != Contact.empty.id || !contact.firstName.isEmpty
    }
    
    var body: some View {
        NavigationStack {
            Form {
                Section("Name") {
                    TextField("First Name", text: $contact.firstName)
                    TextField("Last Name", text: $contact.lastName)
                }
                
                Section("Contact Info") {
                    TextField("Email", text: $contact.email)
                        .keyboardType(.emailAddress)
                        .textInputAutocapitalization(.never)
                    TextField("Phone", text: $contact.phone)
                        .keyboardType(.phonePad)
                }
                
                Section {
                    // Validation feedback (View logic, not business logic)
                    if !contact.isValid && !contact.firstName.isEmpty {
                        Label("Please fill in all required fields", systemImage: "exclamationmark.triangle")
                            .foregroundStyle(.orange)
                    }
                }
            }
            .navigationTitle(isEditing ? "Edit Contact" : "New Contact")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        onCancel()
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        onSave(contact)
                    }
                    .disabled(!contact.isValid)
                }
            }
        }
    }
}

// MARK: - Preview

#Preview {
    ContactListView()
}
