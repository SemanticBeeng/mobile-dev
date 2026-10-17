# MVC Meta-Pattern

> A high-level division of an application's responsibilities into Model, View, and Controller concerns.

## Definition

Model-View-Controller (MVC) is a meta-pattern: it describes a family of related architectural roles rather than prescribing one universal object graph or set of classes. The Model owns application data and domain rules; the View presents information and captures interaction; the Controller coordinates actions and application flow. Frameworks implement these roles differently, so an MVC label alone does not tell you where every line of code belongs.

## In PureMVC

PureMVC makes the roles explicit through registries and collaborating actors. The Model manages Proxies, the View manages Mediators and observers, and the Controller maps Notifications to Commands. A Facade coordinates access to these parts. This is more structured than simply naming a view controller “Controller”: each role has a defined registration and communication responsibility.

## Why It Matters

Separating responsibilities helps prevent view code from becoming the home for data access and application workflows. It also makes the tradeoff visible: explicit actors improve modularity and test seams, but add types, registration steps, and message contracts. MVC is not automatically a layering rule; domain rules should remain independent of presentation when the application requires that boundary.

## Distinctions

- PureMVC is a particular framework implementation of MVC, not the definition of MVC itself.
- UIKit's historical MVC often places substantial coordination in a view controller; PureMVC distributes it among Mediators, Commands, Proxies, and the framework core.
- SwiftUI's state-driven rendering changes how views are built, but it does not prevent an app from using MVC-style domain coordination.

## Related Concepts

- [[Facade]]
- [[Model]]
- [[View]]
- [[Controller]]
- [[MVC]]
- [[PureMVC]]
- [[Glossary]]

## Sources

- [PureMVC](https://puremvc.org/)
- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)