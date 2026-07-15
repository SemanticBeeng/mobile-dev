// MARK: - APP ENTRY POINT
// The App struct is the entry point for a SwiftUI application.

import SwiftUI

@main
struct MVC_001App: App {
    var body: some Scene {
        WindowGroup {
            ContactListView()
        }
    }
}

/*
 MVC_001 - Contact List Demo
 ============================
 
 This example demonstrates the Model-View-Controller pattern in iOS.
 
 COMPONENT RESPONSIBILITIES:
 
 1. MODEL (Contact.swift)
    - Defines data structure (Contact)
    - Contains business logic (validation, formatting)
    - Has NO knowledge of UI
 
 2. VIEW (ContactListView.swift)
    - Displays data passed to it
    - Captures user input
    - Delegates actions to Controller
    - Contains only presentation logic
 
 3. CONTROLLER (ContactController.swift)
    - Owns and manages data
    - Responds to user actions
    - Updates the Model
    - Tells View to update (via @Observable)
 
 DATA FLOW:
 
    User taps "+"
         │
         ▼
    View calls controller.beginAddingContact()
         │
         ▼
    Controller sets isAddingContact = true
         │
         ▼
    @Observable notifies View
         │
         ▼
    View presents sheet
         │
         ▼
    User fills form and taps "Save"
         │
         ▼
    View calls onSave closure -> controller.addContact()
         │
         ▼
    Controller validates (Model) and saves
         │
         ▼
    Controller updates contacts array
         │
         ▼
    View re-renders with new contact
 
 LIMITATIONS OF MVC:
 
 1. "Massive View Controller" - Controllers tend to grow large
 2. Hard to unit test - Controller tightly coupled to View
 3. State management complexity - Who owns what?
 
 NEXT STEPS:
 
 See MVVM_001 for the evolution that addresses these issues.
*/
