# Controller

> The MVC responsibility for coordinating application actions and mapping requests to behavior.

## Definition

In MVC, the Controller interprets an input or request and coordinates the response. A Controller is not necessarily a visual controller, nor should it automatically own persistent domain state. Its precise scope depends on the framework: it may route requests, invoke application behavior, or coordinate collaborators.

## In PureMVC

PureMVC's Controller maps Notification names to Command classes. When a matching Notification is sent, the Controller creates and executes the registered Command. This makes the Controller a command registry and dispatch mechanism rather than a container for all application logic. The Command performs the workflow.

## Design Implications

Notification names and their Command mappings are part of the application's behavior contract. Register mappings during a predictable startup phase, keep each Command focused, and avoid embedding long-lived state in a command whose lifecycle is one execution. If no workflow is needed, a separate Command for every trivial event may add needless ceremony.

## Related Concepts

- [[Command]]
- [[Notification]]
- [[Facade]]
- [[MVC-Meta-Pattern]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC](https://puremvc.org/)