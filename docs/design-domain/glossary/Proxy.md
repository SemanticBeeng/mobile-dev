# Proxy

> A PureMVC Model actor that encapsulates a coherent data responsibility behind a named interface.

## Definition

A Proxy stands in for a resource or data-facing responsibility. It can hold or retrieve data, coordinate access to a service, and provide a named point through which application actors work with that resource. The proxy idea is broader than persistence: a Proxy might wrap remote data, local storage, or another domain service.

## In PureMVC

The Model registers Proxies by name and makes them available through the Facade. A Proxy can communicate with other PureMVC actors by sending Notifications. In the Swift Multicore framework, Proxy inherits notifier behavior and receives the core key as part of registration/lifecycle setup, so its notifications are scoped to its core.

## Design Implications

Choose names and responsibilities that describe the resource or capability, not the screen that happens to use it. Avoid turning one [[Proxy]] into an unbounded repository for the whole application. Since the Swift Notification body is `Any?`, prefer stable payload types and validate boundaries instead of relying on undocumented casts.

## Related Concepts

- [[Model]]
- [[Notification]]
- [[Async-Proxy]]
- [[Multicore-Architecture]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)