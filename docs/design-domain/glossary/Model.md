# Model

> The MVC responsibility for application data, domain rules, and the services that manage them.

## Definition

In MVC, the Model represents the application's domain-facing information and behavior. It should not be confused with a single data structure: the model may include entities, validation, repositories, and services, depending on the architecture. The term names a responsibility boundary, not one required class shape.

## In PureMVC

PureMVC's Model is primarily a registry of Proxies, keyed by Proxy name. It creates and manages these data actors and offers the framework's registration, retrieval, existence-check, and removal operations. The Proxies carry the actual application-specific data responsibilities; the Model registry is not itself the application's database or domain model.

## Design Implications

Keep a Proxy's responsibility coherent and make dependencies such as storage, networking, or repositories explicit within the application design. Do not assume that because a Proxy is registered with the Model, every domain rule belongs inside that Proxy. The framework supplies the registry and lifecycle conventions; the application still chooses its domain boundaries.

## Related Concepts

- [[Proxy]]
- [[MVC-Meta-Pattern]]
- [[Facade]]
- [[Command]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC](https://puremvc.org/)