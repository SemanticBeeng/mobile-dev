// MARK: - MVC_001 with SwiftCrossUI
// Cross-platform contact list demonstrating MVC pattern

import SwiftCrossUI
import GtkBackend
import SharedModels  // Shared Contact model

// MARK: - CONTROLLER

class ContactController: Observable {
    @Observed var contacts: [Contact] = Contact.samples
    @Observed var newFirstName: String = ""
    @Observed var newLastName: String = ""
    @Observed var newEmail: String = ""
    @Observed var newPhone: String = ""
    @Observed var selectedIndex: Int? = nil
    @Observed var statusMessage: String = "Ready"
    
    func addContact() {
        let contact = Contact(
            firstName: newFirstName,
            lastName: newLastName,
            email: newEmail,
            phone: newPhone
        )
        
        guard contact.isValid else {
            statusMessage = "⚠️ Please fill in all required fields"
            return
        }
        
        contacts.append(contact)
        clearForm()
        statusMessage = "✓ Added: \(contact.fullName)"
    }
    
    func deleteSelected() {
        guard let index = selectedIndex, index < contacts.count else {
            statusMessage = "⚠️ Select a contact to delete"
            return
        }
        let name = contacts[index].fullName
        contacts.remove(at: index)
        selectedIndex = nil
        statusMessage = "✓ Deleted: \(name)"
    }
    
    func clearForm() {
        newFirstName = ""
        newLastName = ""
        newEmail = ""
        newPhone = ""
    }
    
    func selectContact(at index: Int) {
        selectedIndex = index
        if index < contacts.count {
            let contact = contacts[index]
            statusMessage = "Selected: \(contact.fullName)"
        }
    }
}

// MARK: - VIEW

struct ContactListApp: App {
    let identifier = "com.example.MVC_001_CrossUI"
    
    let controller = ContactController()
    
    var body: some Scene {
        WindowGroup("MVC_001 - Contact List") {
            ContentView(controller: controller)
        }
    }
}

struct ContentView: View {
    @ObservedObject var controller: ContactController
    
    var body: some View {
        VStack {
            // Header
            Text("📇 Contact List (MVC Demo)")
                .padding(.bottom, 10)
            
            // Contact List
            VStack {
                Text("Contacts (\(controller.contacts.count))")
                
                ForEach(Array(controller.contacts.enumerated()), id: \.element.id) { index, contact in
                    HStack {
                        Text(controller.selectedIndex == index ? "▶" : "  ")
                        Text("\(contact.fullName) - \(contact.email)")
                    }
                    .padding(.vertical, 2)
                }
            }
            .padding(.bottom, 10)
            
            // Add Contact Form
            VStack {
                Text("Add New Contact")
                
                HStack {
                    Text("First Name:")
                    TextField("First", text: $controller.newFirstName)
                }
                
                HStack {
                    Text("Last Name:")
                    TextField("Last", text: $controller.newLastName)
                }
                
                HStack {
                    Text("Email:")
                    TextField("email@example.com", text: $controller.newEmail)
                }
                
                HStack {
                    Text("Phone:")
                    TextField("555-0000", text: $controller.newPhone)
                }
                
                HStack {
                    Button("Add Contact") {
                        controller.addContact()
                    }
                    
                    Button("Delete Selected") {
                        controller.deleteSelected()
                    }
                    
                    Button("Clear") {
                        controller.clearForm()
                    }
                }
            }
            .padding(.top, 10)
            
            // Status Bar
            Text(controller.statusMessage)
                .padding(.top, 10)
        }
        .padding(20)
    }
}

// MARK: - ENTRY POINT

@main
struct Main {
    static func main() {
        ContactListApp.main()
    }
}
