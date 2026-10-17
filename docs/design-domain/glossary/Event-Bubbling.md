# Event Bubbling

> A UI event propagation model in which an event travels from its originating element through ancestor elements.

## Definition

In a bubbling event system, an event begins at a source element and moves upward through a hierarchy until it is handled, stopped, or reaches the hierarchy's root. This is useful for UI interaction because a parent can handle events originating from descendants. Bubbling is tied to a component tree and its event-dispatch rules.

## Contrast with PureMVC Notifications

PureMVC Notifications use observer/publish-subscribe delivery, not parent-to-parent bubbling. A subscriber receives a message because it registered interest in a name, not because it is an ancestor of the sender. A Mediator may listen to ordinary platform UI events and then publish a Notification, bridging the UI event system to application messaging.

## Why the Distinction Matters

Treating Notifications as UI events can lead to misplaced subscriptions and confusion about propagation order. Keep control events within the UI framework and use application Notifications for cross-actor communication. The two mechanisms can coexist, each serving a different boundary.

## Related Concepts

- [[Notification]]
- [[Publish-Subscribe]]
- [[Observer-Pattern]]
- [[Mediator]]
- [[View-Component]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC](https://puremvc.org/)