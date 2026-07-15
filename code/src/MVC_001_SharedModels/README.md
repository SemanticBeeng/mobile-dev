# MVC_001_SharedModels

> Shared domain models for MVC example apps.

## Purpose

This package contains **platform-independent models** that can be shared across:
- iOS apps (MVC_001 via Xcode SPM)
- Linux/macOS apps (MVC_001_CrossUI via SwiftCrossUI)
- Future examples (MVVM, TCA, etc.)

## Models

| Model | Description |
|-------|-------------|
| `Contact` | Contact entity with validation logic |

## Usage

```swift
import SharedModels

let contact = Contact(
    firstName: "John",
    lastName: "Doe",
    email: "john@example.com"
)

print(contact.fullName)  // "John Doe"
print(contact.isValid)   // true
```

## Why Shared Models?

Following **Clean Architecture** principles:
- Models are **framework-independent**
- Business logic lives in the **domain layer**
- UI frameworks import models, not vice versa

```
┌─────────────────────────────────────┐
│     MVC_001 (iOS/SwiftUI)           │
├─────────────────────────────────────┤
│     MVC_001_CrossUI (Linux/GTK)     │
├─────────────────────────────────────┤
│       MVC_001_SharedModels          │  ← Single source of truth
│    (Contact, future models...)      │
└─────────────────────────────────────┘
```

## Adding to Your Package

```swift
dependencies: [
    .package(path: "../MVC_001_SharedModels")
],
targets: [
    .target(
        name: "YourApp",
        dependencies: ["SharedModels"]
    )
]
```
