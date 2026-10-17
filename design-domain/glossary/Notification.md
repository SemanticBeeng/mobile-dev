# Notification

> A named application-level message used by PureMVC actors to communicate without direct references.

## Definition

A Notification communicates that an application event or request has occurred. A sender publishes a message under a name; interested observers receive it without requiring the sender to know each recipient. This supports decoupled communication, but the message name and payload still form a contract that must be documented and maintained.

## In PureMVC

The View's observer mechanism distributes Notifications to registered observers. The Controller also maps Notification names to Commands, allowing a Notification to trigger a workflow. Mediators and Proxies can publish messages through their Notifier behavior; Commands can publish follow-up messages. Notifications need not follow a UI parent-child hierarchy.

The Swift Multicore API represents a Notification with a required string `name`, an optional `Any?` `body`, and an optional `String?` `type`. The flexible payload can carry a value object, but it weakens compile-time guarantees at the framework boundary. Use stable names and typed payload conventions, and test consumers against the expected shape.

## What It Is Not

A PureMVC Notification is not automatically a UI event, a Swift `NotificationCenter` notification, or an event that bubbles through a view tree. UI controls should still use their platform's event mechanisms; a Mediator can translate a UI event into an application-level Notification.

## Related Concepts

- [[Publish-Subscribe]]
- [[Observer-Pattern]]
- [[Event-Bubbling]]
- [[Mediator]]
- [[Command]]
- [[Proxy]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)