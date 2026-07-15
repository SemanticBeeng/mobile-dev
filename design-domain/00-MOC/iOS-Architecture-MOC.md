# iOS App Architecture - Map of Content

> A domain-driven knowledge base for iOS application microarchitecture concepts using Swift and SwiftUI.

## 🎯 Learning Objectives
- Master fundamental iOS architecture patterns
- Understand state management in SwiftUI
- Build scalable, testable, and maintainable iOS applications
- Apply SOLID principles in Swift development

---

## 📚 Architecture Domains

### 1. Architecture Patterns
Core structural patterns for organizing iOS applications.

- [[MVVM]] - Model-View-ViewModel
- [[MVC]] - Model-View-Controller (Apple's traditional pattern)
- [[TCA]] - The Composable Architecture
- [[VIPER]] - View-Interactor-Presenter-Entity-Router
- [[Clean-Architecture]] - Uncle Bob's layered approach
- [[Redux-Pattern]] - Unidirectional data flow

### 2. State Management
Managing application and UI state in SwiftUI.

- [[ObservableObject]] - Reference type state container
- [[StateObject-vs-ObservedObject]] - Ownership semantics
- [[EnvironmentObject]] - Dependency injection via environment
- [[State-and-Binding]] - Value type state management
- [[Combine-Framework]] - Reactive state streams

### 3. Data Layer
Persistence, networking, and data flow patterns.

- [[Repository-Pattern]] - Data source abstraction
- [[DTO-Pattern]] - Data Transfer Objects
- [[Core-Data-Architecture]] - Apple's persistence framework
- [[SwiftData-Architecture]] - Modern persistence layer
- [[Network-Layer-Design]] - API client architecture

### 4. UI Patterns
SwiftUI-specific presentation layer patterns.

- [[View-Composition]] - Building complex UIs from simple views
- [[ViewModifier-Pattern]] - Reusable view modifications
- [[Coordinator-Pattern]] - Navigation and flow control
- [[Container-Presentational]] - Smart vs dumb views

### 5. Dependency Injection
Inversion of control and testability.

- [[Constructor-Injection]] - Dependencies via init
- [[Environment-Injection]] - SwiftUI environment system
- [[DI-Containers]] - Third-party DI frameworks
- [[Protocol-Oriented-Dependencies]] - Abstracting dependencies

### 6. Concurrency & Async
Modern Swift concurrency patterns.

- [[Swift-Concurrency]] - async/await fundamentals
- [[Actors]] - Data race protection
- [[MainActor]] - UI thread safety
- [[Structured-Concurrency]] - Task hierarchies

### 7. Modularization
Scaling codebases with modular architecture.

- [[Feature-Modules]] - Vertical slicing
- [[Swift-Package-Manager]] - Module management
- [[Module-Communication]] - Inter-module coordination

### 8. System Architecture
Understanding distributed systems and client-server patterns.

- [[Client]] - User-facing application component
- [[Server]] - Backend request handler
- [[Client-Server-Architecture]] - Distributed app pattern
- [[SPA-Single-Page-Application]] - Web client architecture
- [[SSR-Server-Side-Rendering]] - Server-rendered web apps  
- [[BFF-Pattern]] - Backend for Frontend
- [[Offline-First-Architecture]] - Network-independent apps
- [[MVC-Across-Architectures]] - MVC in different contexts

---

## 🗺️ Learning Roadmap

```
Level 1: Foundations
├── [[State-and-Binding]]
├── [[ObservableObject]]
├── [[View-Composition]]
└── [[MVVM]]

Level 2: Intermediate
├── [[Repository-Pattern]]
├── [[Coordinator-Pattern]]
├── [[Swift-Concurrency]]
└── [[Protocol-Oriented-Dependencies]]

Level 3: Advanced
├── [[TCA]]
├── [[Clean-Architecture]]
├── [[Feature-Modules]]
└── [[Actors]]

Level 4: System Design
├── [[Client-Server-Architecture]]
├── [[BFF-Pattern]]
├── [[Offline-First-Architecture]]
└── [[MVC-Across-Architectures]]
```

---

## 🏷️ Tags
#ios #swift #swiftui #architecture #mobile-dev
