# PureMVC Glossary

> A linked reference for the concepts used in the PureMVC architecture note.

## Core MVC Actors

- [[Facade]] - Framework entry point and coordination API.
- [[Model]] - Registry for data-facing Proxies.
- [[View]] - Registry for Mediators and notification observers.
- [[Controller]] - Maps notification names to Commands.
- [[Proxy]] - Encapsulates a data or service responsibility.
- [[Mediator]] - Adapts a UI component to PureMVC actors.
- [[Command]] - Executes an application workflow.
- [[Notification]] - Named message distributed to interested actors.

## Architecture and Messaging

- [[MVC-Meta-Pattern]] - Separation of Model, View, and Controller responsibilities.
- [[Observer-Pattern]] - One-to-many notification mechanism.
- [[Publish-Subscribe]] - Decoupled message distribution by interest.
- [[Event-Bubbling]] - Hierarchical UI event propagation, contrasted with Notifications.
- [[View-Component]] - Concrete UI element mediated by PureMVC.

## Core Lifecycle and Modularization

- [[Singleton]] - One shared core instance.
- [[Multiton]] - One instance per key.
- [[Multicore-Architecture]] - Multiple independent PureMVC cores in one process.
- [[Multiton-Key]] - Identifier used to address a Multicore instance.

## Companion Utilities

- [[Async-Command]] - Asynchronous command workflows.
- [[Async-Proxy]] - Asynchronous data operations behind a Proxy.
- [[Pipes]] - Explicit message routing across connected endpoints.
- [[State-Machine]] - Explicit states and permitted transitions.

## Source

- [[PureMVC]] - Framework design, actors, and usage tradeoffs.