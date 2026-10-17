# Multiton

> An instance-management pattern that keeps one instance per key rather than one instance total.

## Definition

A Multiton maintains a keyed collection of instances. Each key identifies a distinct instance, while repeated requests for the same key return the same instance. This provides named isolation within one process, but callers must use the correct key and the registry must define cleanup and key-collision behavior.

## In PureMVC

PureMVC Multicore uses a Multiton core instead of the Standard variant's Singleton. Each core is addressed by a `multitonKey` and owns its own Facade, Model, View, Controller, and actor registrations. The instances are independent framework namespaces, not separate operating-system processes.

## Design Implications

Use distinct, stable keys for independently operating subsystems. Treat key assignment as part of registration and initialization, and ensure test teardown removes or isolates core instances. A Multiton can make module boundaries practical, but it does not automatically prevent actors from depending on one another through cross-core APIs or shared global resources.

## Related Concepts

- [[Singleton]]
- [[Multicore-Architecture]]
- [[Multiton-Key]]
- [[Facade]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)
- [PureMVC](https://puremvc.org/)