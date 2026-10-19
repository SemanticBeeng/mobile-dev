# PureMVC User Registry Design Runbook

> Implementation rules for the Kotlin PureMVC user-registry sample. Rule IDs are stable three-digit identifiers for discussion, review, and future change tracking.

## Scope

This runbook captures the design principles and patterns used by the sample: framework-independent domain logic, PureMVC actor responsibilities, local JSON persistence, terminal UI separation, and validation/testing boundaries. Apply the rules when extending the sample; a proposed exception should identify the rule and explain its tradeoff.

## Application Flow

The diagram traces a request from terminal input through PureMVC control routing and the data path to JSON storage. Solid messages show user actions, framework control, and response notifications; notes and payload labels identify the domain data being passed or persisted.

The Facade routes notifications through PureMVC's View observer registry to registered Commands and Mediators. Commands retrieve the Proxy through the Facade and Model.

```mermaid
sequenceDiagram
	actor User
	participant Screen as Terminal Screen
	participant Mediator as UserRegistryMediator
	participant Facade as PureMVC Facade
	participant Controller as PureMVC Controller
	participant Command as Mapped Command
	participant Model as PureMVC Model
	participant Proxy as UserRegistryProxy
	participant Factory as UserFactory
	participant Entity as User Entity
	participant Rules as UserRegistryRules
	participant Repository as UserRepository / JsonUserRepository
	participant File as Versioned JSON File
	participant View as PureMVC View / Observer Registry

	User->>Screen: Select action and enter fields
	Screen->>Mediator: UserAction (UI event)
	Mediator->>Facade: Request Notification + typed payload
	Facade->>Controller: Route notification name
	Controller->>Command: Execute mapped Command
	Command->>Facade: retrieveProxy(UserRegistryProxy.NAME)
	Facade->>Model: Look up registered Proxy
	Model-->>Facade: UserRegistryProxy
	Facade-->>Command: Proxy reference

	alt Create or update user
		Command->>Proxy: create/update(UserDraft)
		Proxy->>Factory: Normalize draft and prepare create/update
		Factory->>Factory: Trim names; lowercase/trim email
		Factory->>Entity: Construct or copy canonical User
		Entity->>Entity: Enforce entity invariants
		Entity-->>Factory: Valid User
		Factory-->>Proxy: Valid User
		Proxy->>Rules: requireUnique(candidate registry)
		Rules-->>Proxy: Accept or reject registry
		Proxy->>Repository: save(updated users)
		Repository->>Repository: Encode schema-versioned JSON; write temp; replace file
		Repository->>File: Persist document
		File-->>Repository: Write result
		Repository-->>Proxy: Save result
	else Initial load or list/search
		Command->>Proxy: load/list/search()
		Proxy->>Repository: load()
		Repository->>File: Read JSON document
		File-->>Repository: Document bytes
		Repository->>Entity: Construct User from each record
		Entity->>Entity: Enforce entity invariants
		Entity-->>Repository: Validated User
		Repository->>Rules: requireUnique(loaded users)
		Rules-->>Repository: Accept or reject registry
		Repository-->>Proxy: User list
	end

	Proxy-->>Command: Operation result / User data
	Command->>Facade: SCREEN_UPDATE Notification + ScreenUpdate
	Facade->>View: Notify registered observers
	View->>Mediator: Deliver SCREEN_UPDATE
	Mediator->>Screen: present(ScreenUpdate)
	Screen-->>User: Render result or error
```

Validation or storage exceptions are converted by the Command into an error `ScreenUpdate`; they do not bypass the Mediator to write directly to the terminal. For reads, JSON record mapping constructs `User` values before the shared registry uniqueness policy accepts the loaded collection.

## Rules

### 001 - Keep Entity Invariants in the Domain

The `User` domain entity owns its canonical invariants: nonblank ID, trimmed bounded names, normalized valid email, and valid creation timestamp. Its constructor is the final guard, including when a `User` is reconstructed from persisted data. `UserRegistryProxy` must not repeat these per-user checks; it delegates draft creation and update to `UserFactory`.

**Applied with:** `User` constructor validation in `User.kt`.

### 002 - Normalize Drafts at One Domain Boundary

`UserFactory` converts a `UserDraft` into canonical names and email before constructing or updating a `User`. Keep normalization in one domain boundary; the UI and Proxy pass drafts through instead of maintaining their own field-validation rules. The `User` constructor then checks that the resulting entity is valid.

**Applied with:** `UserFactory.create` and `UserFactory.update` in `User.kt`.

### 003 - Separate Entity Rules from Registry Rules

Rules that need one user belong to the entity. Rules that compare multiple users, such as unique IDs and email addresses, belong to a domain-level registry policy. Do not put collection-wide uniqueness checks in `User` or keep a second `ensureEmailAvailable` implementation in the Proxy.

**Applied with:** `UserRegistryRules.requireUnique` in `User.kt`, replacing the Proxy-local email-only check with one policy covering both unique IDs and emails.

### 004 - Share Policy; Enforce at Trust Boundaries

Keep one implementation of each collection invariant, then invoke it where invalid state can enter or be persisted. The Proxy checks proposed changes before updating in-memory state; the JSON repository checks loaded and saved registries. Reusing `UserRegistryRules.requireUnique` at these independent boundaries is deliberate defense, not duplicated validation logic. Do not re-create a slightly different uniqueness rule at either call site.

**Applied with:** `UserRegistryProxy` and `JsonUserRepository` calling `UserRegistryRules.requireUnique`.

### 005 - Preserve Identity and Lifecycle Metadata on Update

An edit changes only the fields the user is allowed to edit. It preserves the existing ID, active status, and creation timestamp. Do not manufacture a replacement entity with new identity or lifecycle metadata merely to reuse creation logic.

**Applied with:** `UserFactory.update`, which copies the existing `User` with normalized editable fields.

## Validation Refactor: Principles and Patterns

The refactor removes user-field validation from `UserRegistryProxy` and gives each rule one domain owner. The split is intentional: `User` protects entity validity, while `UserRegistryRules` protects invariants that span a collection.

| Principle or pattern | Application in this refactor |
|---|---|
| **Single responsibility** | `User` validates its state; `UserFactory` normalizes drafts and creates/updates entities; `UserRegistryRules` enforces collection-wide constraints; the Proxy coordinates registry operations. |
| **DRY / single source of truth** | Name/email normalization and uniqueness each have one implementation. The Proxy and repository call the same uniqueness policy rather than carrying duplicate predicates. |
| **Domain entity with invariant enforcement** | `User` validates every constructed instance, including entities mapped from JSON. |
| **Factory pattern** | `UserFactory` translates raw `UserDraft` input into canonical entity values and preserves lifecycle fields during updates. |
| **Domain policy object** | `UserRegistryRules` expresses rules about the collection, which cannot be decided by inspecting one `User`. |
| **Fail-fast validation** | Invalid entities or registries are rejected before they become the Proxy's updated in-memory state or are written to disk. |
| **Defense at trust boundaries** | The Proxy protects use-case mutations, and the JSON repository protects storage load/save. Both rely on the same policy implementation. |
| **Dependency inversion** | The Proxy uses the `UserRepository` abstraction and does not know that the concrete adapter uses JSON files. |

`UserRegistryRules` currently lives alongside the domain model in `User.kt`; its policy responsibility is logically separate even though it shares the file. Keep it there while small, and extract it to a dedicated source file only if the domain rules grow enough to justify that separation.

### 006 - Depend on Repository Abstractions

The registry Proxy depends on `UserRepository`, not on filesystem APIs, JSON serialization, or a concrete storage implementation. Keep JSON encoding, schema handling, and file replacement inside `JsonUserRepository` so storage can be replaced by an in-memory fake in tests.

**Patterns:** [[Repository-Pattern]], dependency inversion.

### 007 - Give PureMVC Actors One Clear Responsibility

Use the Facade for composition and access; Model for Proxy registration; Proxy for registry state and repository coordination; Controller for Notification-to-Command mapping; Commands for application actions; Mediator for translating UI actions and results; and the terminal screen for input and rendering. Do not move all application behavior into the Mediator or treat PureMVC's View registry as the actual UI.

**Pattern:** [[PureMVC]].

### 008 - Keep UI and Domain Independent

The domain must not depend on PureMVC, Kotter, terminal input/output, or JSON. The UI reports user actions through the Mediator; it does not enforce domain invariants or access persistence directly. Presentation-specific formatting stays in the terminal adapter.

**Patterns:** [[Clean-Architecture]], [[Mediator]].

### 009 - Make Application Messages Explicit

Use stable named Notifications for cross-actor requests and results. Keep message payloads small and purposeful; prefer typed Kotlin payloads at application boundaries over unstructured values or implicit shared state. Notifications are application messages, not a replacement for terminal events or direct calls between tightly owned operations.

**Pattern:** [[Publish-Subscribe]].

### 010 - Keep Commands Focused and Stateless

Each Command handles one user-registry workflow, delegates entity and collection rules to the domain, and delegates durable storage through the Proxy and repository. Commands should not become long-lived state containers or absorb UI rendering and file handling.

**Pattern:** [[Command]].

### 011 - Make Persistence Versioned and Failure-Safe

Store a schema version with the JSON registry. Reject unsupported versions and malformed data without overwriting the original file. Write to a sibling temporary file and replace the target atomically when supported. Treat a missing file as an empty registry; create it on the first successful write.

**Applied with:** `JsonUserRepository`.

### 012 - Test at Each Architectural Boundary

Test domain normalization and invariants without PureMVC or disk I/O; test JSON round-tripping and corrupt-file preservation with temporary files; and test representative user workflows through the real Facade, Controller, Command, Proxy, and Mediator using fake repository and screen adapters. Tests should prove behavior and boundary contracts, not implementation trivia.

## Change Review Checklist

- Does the change preserve all existing `User` invariants?
- Is a new rule entity-level or registry-level, and is it implemented in one domain location?
- Does the change keep domain code independent of framework, UI, and persistence details?
- Are PureMVC responsibilities and message contracts still explicit?
- Are persistence failure and schema-version behaviors safe?
- Is there a focused test at the affected boundary?

## Related Concepts

- [[PureMVC]]
- [[Glossary]]
- [[Repository-Pattern]]
- [[Clean-Architecture]]
- [[Publish-Subscribe]]
- [[Command]]
- [[Mediator]]


----

