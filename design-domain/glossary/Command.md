# Command

> A discrete application action invoked by the PureMVC Controller in response to a Notification.

## Definition

A Command represents a unit of application behavior: perform a use case, coordinate collaborators, and produce an outcome. It gives a workflow a clear entry point that can be invoked and tested independently from a UI component. Commands are generally action-oriented; they are not a natural home for long-lived state.

## In PureMVC

The Controller maps a Notification name to a Command. Sending that Notification causes the mapped Command to execute with access to the notification and the appropriate core. A Command may retrieve Proxies through the Facade, call their capabilities, and publish result Notifications. The framework provides a simple command base and a MacroCommand that sequences other Commands.

## Design Implications

Treat the notification name, payload shape, and expected side effects as an application contract. Keep command execution focused, make external dependencies testable, and decide how failures are represented and reported. The core framework's synchronous command model does not itself make network or other work asynchronous; the companion Async Command utility addresses that separate concern.

## Related Concepts

- [[Controller]]
- [[Notification]]
- [[Proxy]]
- [[Async-Command]]
- [[Facade]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)