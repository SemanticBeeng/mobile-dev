# Facade

> A simplified public interface through which application code can reach a subsystem's coordinated services.

## Definition

The Facade pattern hides a subsystem's internal collaborators behind a smaller, stable interface. Clients ask the Facade to perform or locate work rather than acquiring and wiring every implementation object themselves. A Facade can reduce knowledge of internal structure, but it should not become a dumping ground for domain rules; coordination belongs there, while feature behavior belongs in the responsible collaborators.

## In PureMVC

The PureMVC Facade is the framework's usual entry point. It coordinates the Model, View, and Controller and exposes operations such as registering, retrieving, and removing Proxies and Mediators, registering Commands, and sending Notifications. It is available to framework actors through their notifier behavior, so actors can collaborate without directly depending on the core implementation classes.

In Multicore, a Facade belongs to a particular keyed core. Looking up the wrong key means addressing a different subsystem, not merely a different service instance.

## Design Implications

The Facade is useful as a composition boundary and framework API. Because it can expose many operations, avoid routing every method call through it by habit: use it for PureMVC registration and message coordination, not as a global service locator for unrelated app dependencies.

## Related Concepts

- [[MVC-Meta-Pattern]]
- [[Model]]
- [[View]]
- [[Controller]]
- [[Multicore-Architecture]]
- [[Multiton-Key]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)