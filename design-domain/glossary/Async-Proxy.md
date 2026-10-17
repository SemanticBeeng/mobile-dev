# Async Proxy

> A PureMVC companion utility for representing asynchronous data or service operations behind a Proxy.

## Definition

An asynchronous Proxy provides a stable application-facing interface for data operations that complete later. It can encapsulate remote requests, callbacks, or other asynchronous sources, keeping those mechanics out of UI components and Commands. It must communicate success, failure, and cancellation as deliberately as it communicates the successful value.

## In the PureMVC Ecosystem

The Swift Multicore project lists Async Proxy as a separate utility. It extends the Proxy data role; it is not a core framework actor distinct from Proxy. Its purpose is to support asynchronous data workflows while preserving the framework's actor boundaries and notification-based communication.

## Design Considerations

Avoid ambiguous “loading finished” messages that cannot identify which request or result they describe. Use typed domain results internally, account for overlapping requests and stale responses, and keep UI-thread or actor isolation explicit. Verify the utility's supported Swift version and API before integrating it.

## Related Concepts

- [[Proxy]]
- [[Model]]
- [[Notification]]
- [[Async-Command]]
- [[Multicore-Architecture]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)
- [PureMVC Swift Async Proxy Utility](https://github.com/PureMVC/puremvc-swift-util-asyncproxy/wiki)