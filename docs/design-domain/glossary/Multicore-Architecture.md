# Multicore Architecture

> An application architecture in which multiple independent framework cores coexist in one process.

## Definition

Multicore architecture partitions a program into subsystems that have their own coordination state and registrations. In PureMVC, a core is an instance of the framework's MVC machinery, not a CPU core or a separate process. This can let teams or features evolve with less naming and registration collision, while preserving a common application runtime.

## In PureMVC

Each Multicore instance has its own Facade, Model, View, Controller, and managed actors. The core is selected by a key, and actor notifications resolve against the matching core. The Swift implementation assigns keys during framework-managed registration or command execution; an actor cannot reliably use its Facade before it has a valid key.

## Benefits and Boundaries

Independent registries reduce accidental collisions and make subsystem ownership explicit. They do not provide process isolation, security isolation, separate scheduling, or automatic dependency inversion between modules. Shared storage, global services, cross-core communication, and lifecycle still need deliberate design.

## When It Helps

It is most useful when a product contains independently owned subsystems, plug-in-like modules, or multiple application contexts that need the same framework architecture. For a small feature set, the setup and key lifecycle may cost more than the isolation is worth.

## Related Concepts

- [[Multiton]]
- [[Multiton-Key]]
- [[Singleton]]
- [[Facade]]
- [[Notification]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)
- [PureMVC](https://puremvc.org/)