# Observer Pattern

> A behavioral pattern in which a subject notifies registered observers when an event or state change is published.

## Definition

The Observer pattern establishes a one-to-many relationship: an observable subject maintains or delegates a set of observers, then notifies them when something relevant happens. Observers can react without the subject needing concrete knowledge of each observer. The pattern reduces direct coupling, though it introduces subscription lifecycle, ordering, and message-contract questions.

## In PureMVC

PureMVC implements an observer mechanism in its View. Mediators register interest in Notification names; when a Notification is sent, matching observers receive it. The Controller also uses notification names as command dispatch keys. The framework's observer is an internal delivery mechanism; application actors usually interact through the Facade and Notifications rather than manipulating observer objects directly.

## Observer vs Publish-Subscribe

The terms overlap but emphasize different structures. Observer often describes a subject with explicit observer registrations. Publish-subscribe emphasizes publishers and subscribers communicating through a broker or named channel without direct subject-subscriber references. PureMVC uses observer machinery to deliver notifications with publish/subscribe-style decoupling.

## Design Implications

Define who owns registration and removal, ensure observers are registered in the correct core, and avoid hidden ordering assumptions. A notification with no observers may indicate a valid optional response or a missing registration; tests should make intended behavior explicit.

## Related Concepts

- [[Publish-Subscribe]]
- [[Notification]]
- [[View]]
- [[Mediator]]
- [[Multicore-Architecture]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC](https://puremvc.org/)