# View

> The MVC responsibility for presenting application information and collecting user interaction.

## Definition

The View is the user-facing representation of application state. It renders information and exposes interaction points, while avoiding ownership of unrelated domain decisions. In declarative UI systems, the view describes how current state should appear; it may still contain local presentation logic and event handling.

## In PureMVC

PureMVC's View is a framework registry. It manages Mediators and notification observers, dispatching received Notifications to interested observers. The visible UI is a separate view component; a Mediator connects that component to the framework. Thus, PureMVC's `View` class is not a screen or widget and does not render pixels itself.

## Design Implications

Treat UI events and application messages as different boundaries. A SwiftUI button action or UIKit control event is first handled by the view or its Mediator; the Mediator may then send a PureMVC Notification to request application behavior. The View registry should not be mistaken for a replacement for SwiftUI's rendering and state system.

## Related Concepts

- [[Mediator]]
- [[View-Component]]
- [[Notification]]
- [[Observer-Pattern]]
- [[MVC-Meta-Pattern]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC](https://puremvc.org/)