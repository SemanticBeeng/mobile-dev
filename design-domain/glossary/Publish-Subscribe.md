# Publish-Subscribe

> A messaging pattern in which publishers emit named messages and subscribers receive messages of interest without direct sender-recipient references.

## Definition

Publish-subscribe separates the party that publishes a message from the parties that consume it. A broker or dispatcher routes messages based on topics or message identities. Publishers need not know subscriber identities, and subscribers need not hold references to publishers. This helps modularize communication, but makes message contracts, tracing, and execution order less visible than direct calls.

## In PureMVC

PureMVC Notifications are distributed to observers registered for matching names. A Proxy, Mediator, or Command can publish a Notification, and Mediators can subscribe to relevant names. The Controller can additionally route selected names to Commands. As a result, one message can communicate across MVC actors without a parent-child object relationship.

## Design Implications

Use named messages for meaningful application-level communication, not as a universal substitute for direct function calls. Define payload contracts, choose stable names, and make subscriptions visible at registration points. Be deliberate about fan-out: multiple observers may react to the same message, and the sender may not know which ones exist.

## Related Concepts

- [[Notification]]
- [[Observer-Pattern]]
- [[Controller]]
- [[Mediator]]
- [[Proxy]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore API Documentation](https://puremvc.org/pages/docs/Swift/multicore/)
- [PureMVC](https://puremvc.org/)