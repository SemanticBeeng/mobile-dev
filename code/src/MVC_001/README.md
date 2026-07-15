# MVC_001 - Contact List

> A simple contact list app demonstrating the **Model-View-Controller** pattern.

## Learning Objectives

- Understand MVC component roles
- See clear separation of Model, View, and Controller
- Recognize MVC limitations that lead to MVVM

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         USER                                │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────┐    ┌────────────────┐    ┌─────────────────┐  │
│  │  View   │◄───│   Controller   │───►│     Model       │  │
│  │ (UI)    │    │ (Coordination) │    │ (Data + Logic)  │  │
│  └─────────┘    └────────────────┘    └─────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Files

| File | Role | Description |
|------|------|-------------|
| `Contact.swift` | **Model** | Data structure and business logic |
| `ContactListView.swift` | **View** | UI display layer |
| `ContactController.swift` | **Controller** | Mediates Model ↔ View |
| `MVC_001App.swift` | Entry | App entry point |

## Key Concepts Demonstrated

1. **Model** - Pure data, no UI knowledge
2. **View** - Displays data, reports user actions
3. **Controller** - Owns data, updates view, handles actions

## Run Instructions

Open in Xcode as a Swift Playground or create an iOS app project with these files.

## Related Patterns

- See [[MVC]] in design-domain for full concept
- Next: [[MVVM]] - Evolution of MVC for better testability
