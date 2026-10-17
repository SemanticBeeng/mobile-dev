# State Machine

> A model of behavior that defines named states and the events or conditions that cause transitions between them.

## Definition

A state machine makes state-dependent behavior explicit. It defines a finite set of states, the valid transitions between them, and the events or guards that permit those transitions. This prevents scattered Boolean checks from allowing impossible combinations and gives transition behavior a testable representation.

## In the PureMVC Ecosystem

The Swift Multicore project lists State Machine as a companion utility. In PureMVC applications, it can coordinate application states and transitions in relation to framework messages and commands. It is an optional utility, not a mandatory part of the core MVC actor set.

## Design Considerations

Use a state machine when behavior truly depends on a constrained lifecycle, such as authentication, a checkout flow, or a multi-step task. Define legal transitions and side effects at the boundary; do not duplicate the same state in the state machine, a Proxy, and a view model without a clear source of truth. Verify the utility's current API and transition semantics before depending on them.

## Related Concepts

- [[Notification]]
- [[Command]]
- [[Multicore-Architecture]]
- [[MVC-Meta-Pattern]]
- [[Glossary]]

## Sources

- [PureMVC Swift Multicore Framework Wiki](https://github.com/PureMVC/puremvc-swift-multicore-framework/wiki)
- [PureMVC Swift State Machine Utility](https://github.com/PureMVC/puremvc-swift-util-statemachine/wiki)