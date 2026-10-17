# Singleton

> A creational pattern that restricts a type to one shared instance and provides a global access point to it.

## Definition

A Singleton ensures a single instance exists within a defined runtime scope. It is sometimes used for process-wide coordination, but global access can hide dependencies, complicate tests, and make independent application instances difficult. “One instance” is only meaningful when the scope is clear: process, app, thread, or another container.

## In PureMVC

The Standard PureMVC framework uses a Singleton core. Its Facade and related Model, View, and Controller are shared through that core, so the architecture assumes one application core in the process. The Multicore variant exists for applications that need more than one independent core.

## Design Implications

Singleton is an instance-management policy, not a synonym for “service” or “global variable.” Prefer explicit composition when multiple instances, isolated tests, or app extensions are possible. If a Singleton is used, keep its scope and lifecycle explicit and avoid allowing it to become an unbounded dependency locator.

## Related Concepts

- [[Multiton]]
- [[Multicore-Architecture]]
- [[Facade]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)
- [PureMVC](https://puremvc.org/)