# Mediator

> A PureMVC View actor that adapts a UI component to application-level framework messages.

## Definition

A Mediator centralizes the communication between a UI component and other parts of an application. It observes the component's ordinary UI events, translates user intent into application requests, and translates application updates into presentation changes. This reduces direct coupling between a view component and domain or framework actors.

## In PureMVC

The View registers and manages Mediators by name. A Mediator exposes the component it mediates, declares which Notifications it wants to receive, and handles those Notifications. It can send Notifications through its Notifier base behavior. In Multicore, the framework assigns its core key when it is registered with the View.

## Design Implications

Use a Mediator as an adapter, not as a second view model that accumulates domain logic. Keep view-specific formatting and event translation close to the UI; delegate workflows to Commands and data responsibilities to Proxies. In SwiftUI, avoid fighting declarative state updates: a Mediator should bridge to the view's state interface rather than imperatively reconstructing the UI.

## Related Concepts

- [[View]]
- [[View-Component]]
- [[Notification]]
- [[Command]]
- [[MVC-Meta-Pattern]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC](https://puremvc.org/)