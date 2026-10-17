# View Component

> A concrete UI element or screen that renders information and captures user interaction.

## Definition

A view component is the platform-specific UI object that users see and interact with: a screen, control, composed view, or other display element. It owns presentation and UI-level interaction, not the entire application workflow. Components may expose state or events through bindings, callbacks, delegates, or framework-specific APIs.

## In PureMVC

The view component is distinct from PureMVC's `View` registry. A Mediator is registered with the framework to connect a component's UI events and data to application-level Notifications. The component does not need to know which Command handles a request or which Proxy owns the data.

## SwiftUI Considerations

SwiftUI views are value descriptions that update from state; they are not UIKit view-controller instances. A PureMVC integration should adapt to that state model rather than assume an imperative view object can be mutated in place. Keep `@State`, bindings, and rendering behavior in their appropriate UI roles, with Mediators translating at the boundary.

## Related Concepts

- [[View]]
- [[Mediator]]
- [[Event-Bubbling]]
- [[MVC-Meta-Pattern]]
- [[Glossary]]

## Sources

- [PureMVC](https://puremvc.org/)
- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)