# Async Command

> A PureMVC companion utility for coordinating command workflows that include asynchronous work.

## Definition

An asynchronous command mechanism allows a workflow to suspend while external work completes, such as a network request, without blocking the caller's execution thread. The design must define completion, error, cancellation, and ordering behavior; “async” alone does not guarantee responsiveness or safe state access.

## In the PureMVC Ecosystem

The Swift Multicore wiki lists Async Command as a companion utility. It extends the core Command concept for asynchronous workflows; it is not one of the core MVC actors. Use it when a command's use case genuinely crosses an asynchronous boundary, not simply to add an async wrapper around synchronous work.

## Design Considerations

Make completion and failure explicit, ensure UI-facing updates occur on the appropriate actor or executor, and define what happens if a core or screen is removed before the work finishes. Keep command lifetime and callback ownership clear. Confirm the utility's current API and concurrency model before adopting it, since framework versions and Swift concurrency capabilities can differ.

## Related Concepts

- [[Command]]
- [[Controller]]
- [[Notification]]
- [[Async-Proxy]]
- [[Multicore-Architecture]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)
- [PureMVC Swift Async Command Utility](https://github.com/PureMVC/puremvc-swift-util-asynccommand/wiki)