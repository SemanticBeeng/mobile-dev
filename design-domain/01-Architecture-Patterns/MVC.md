# MVC - Model-View-Controller

> Apple's traditional architecture pattern for iOS development.

## Overview
MVC is Apple's recommended pattern where Controller mediates between Model and View. While common in UIKit, it's less prevalent in SwiftUI.

## Components

```
┌─────────┐         ┌────────────┐         ┌─────────┐
│  Model  │◄───────►│ Controller │◄───────►│  View   │
└─────────┘         └────────────┘         └─────────┘
```

### Model
- Data and business logic
- Notifies Controller of changes
- Independent of UI

### View
- Visual representation
- User interaction capture
- Reusable components

### Controller
- Mediates Model-View
- Handles user actions
- Updates View from Model

## UIKit Implementation

```swift
// Model
struct Contact {
    var name: String
    var phone: String
}

// Controller
class ContactViewController: UIViewController {
    private var contacts: [Contact] = []
    private let tableView = UITableView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupTableView()
        loadContacts()
    }
    
    private func loadContacts() {
        contacts = ContactService.shared.fetchAll()
        tableView.reloadData()
    }
}

extension ContactViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        contacts.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        let contact = contacts[indexPath.row]
        cell.textLabel?.text = contact.name
        return cell
    }
}
```

## The "Massive View Controller" Problem
Controllers tend to accumulate:
- Networking logic
- Data formatting
- Navigation
- Delegation
- View configuration

## Mitigation Strategies

```swift
// Extract data source
class ContactsDataSource: NSObject, UITableViewDataSource {
    var contacts: [Contact] = []
    // ...
}

// Extract formatting
extension Contact {
    var displayName: String {
        name.capitalized
    }
}

// Extract networking
class ContactService {
    func fetchAll() async -> [Contact] { ... }
}
```

## MVC in SwiftUI
SwiftUI naturally evolves MVC into MVVM:
- Views are declarative (not passive)
- No explicit Controller needed
- State management replaces Controller duties

## Pros & Cons

| Pros | Cons |
|------|------|
| Simple to understand | Massive View Controller |
| Apple documented | Poor testability |
| Quick prototyping | Tight coupling |
| UIKit native | Doesn't scale |

## When to Use
- Simple UIKit apps
- Quick prototypes
- Learning iOS basics
- Apple sample code

## Related Patterns
- [[MVVM]] - Evolution for better testability
- [[VIPER]] - More granular separation
- [[Coordinator-Pattern]] - Extract navigation
- [[MVC-Across-Architectures]] - MVC in client-server & web contexts

## See Also
- [[../code/src/MVC_001/README|MVC_001 Example]] - Working code demo

## Tags
#architecture #mvc #uikit #apple #traditional
