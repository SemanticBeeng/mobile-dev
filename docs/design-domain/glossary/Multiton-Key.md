# Multiton Key

> The identifier used to select a particular Multiton-managed PureMVC core.

## Definition

A Multiton key names one instance in a keyed collection. It is both an addressing mechanism and a boundary: code using one key reaches that instance's registrations and collaborators, while a different key selects another. A key is not inherently a security token or a guarantee that two modules cannot share other resources.

## In PureMVC

The Swift Multicore framework calls this identifier the `multitonKey`. Facades use it to locate the core; Commands and Mediators receive it through framework lifecycle operations such as execution and registration. Notifier-based actors use the key to resolve their Facade and send Notifications into the correct core.

## Lifecycle Guidance

Create stable keys at subsystem composition boundaries, pass them consistently during registration, and avoid deriving them from transient UI state. Do not send notifications or resolve the Facade before the actor has been initialized with its key. Tests should use isolated keys or cleanly remove the core to prevent state leaking between cases.

## Related Concepts

- [[Multiton]]
- [[Multicore-Architecture]]
- [[Facade]]
- [[Notification]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)
- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)