# Pipes

> A PureMVC utility for routing messages through explicit pipe connections between actors or subsystems.

## Definition

Pipes model communication as connected message channels. Producers write messages to outputs, consumers read them from inputs, and pipes connect those endpoints. This makes routing topology explicit and can support filters, junctions, and more complex flows than broadcasting a message to every observer.

## In the PureMVC Ecosystem

The PureMVC Swift Multicore wiki lists Pipes as a companion utility, separate from the core framework. It can be used when modules need explicit message pathways or when notifications' broadcast semantics are too broad. Pipes do not replace the Facade, Model, View, Controller, or their registries.

## Design Considerations

Choose pipes when routing topology is a first-class requirement, such as connecting independently composed subsystems. Define message ownership, back-pressure or buffering expectations, lifecycle, and failure handling. For simple in-core application events, ordinary Notifications may be easier to understand and maintain.

## Related Concepts

- [[Notification]]
- [[Publish-Subscribe]]
- [[Multicore-Architecture]]
- [[Facade]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)
- [PureMVC Swift Pipes Utility](https://github.com/PureMVC/puremvc-swift-util-pipes/wiki)