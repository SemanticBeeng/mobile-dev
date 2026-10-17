# PureMVC User Registry

A small Kotlin/JVM terminal application that demonstrates PureMVC Multicore with a local, versioned JSON registry. It is designed to run in a GitHub Codespace without Android or graphical dependencies.

## Requirements

- JDK 17 or newer
- The checked-in Gradle wrapper
- Network access on the first build to resolve Kotlin, Kotter, serialization, and test dependencies

The official PureMVC Kotlin source is included as a local Gradle module because the published artifact listed in its wiki could not be found in Maven Central. See `puremvc-kotlin-multicore/UPSTREAM.md` for its pinned upstream revision and license.

## Run

```sh
./gradlew run
./gradlew run --args="--data-file ./data/users.json --seed-demo"
./gradlew run --args="--plain --data-file ./data/users.json"
./gradlew test
```

The default store is `./data/users.json`. It is created on the first successful write. The TUI uses Kotter; `--plain` selects a line-oriented terminal fallback suitable for limited ANSI terminals. `--seed-demo` adds sample users only when the registry is empty. Use `--help` to see command-line options.

This is a user directory, not an authentication system. It stores no passwords or credentials. Email addresses are trimmed, lowercased, and unique without regard to case. User IDs are UUIDs; the UI displays their first eight characters and accepts a unique prefix for edits, status changes, and removal.

## Architecture

```text
Kotter / Plain terminal
        | UserAction callback
UserRegistryMediator
        | PureMVC Notifications
Controller -> Command -> UserRegistryProxy -> UserRepository
                                             |
                                      JsonUserRepository
                                             |
                                      versioned JSON file
```

- **Facade**: `UserRegistryApplication` creates the Multicore and registers actors.
- **Model**: PureMVC's registry holds `UserRegistryProxy`.
- **Proxy**: owns the loaded registry state and delegates durable storage to `UserRepository`.
- **Controller and Commands**: map named request Notifications to focused user workflows.
- **View registry and Mediator**: PureMVC's View manages the Mediator; the Mediator adapts terminal actions and screen updates.
- **Domain**: `UserFactory` validates user data without depending on PureMVC, Kotter, or JSON.
- **Persistence**: `JsonUserRepository` maps domain objects to a versioned document and replaces the file atomically where supported.

The registry is intentionally single-process and synchronous. Notifications are in-process application messages, not a replacement for terminal input events. The optional `--seed-demo` action demonstrates another Command; it does not create another Multicore.

## Data Safety

An absent file means an empty registry. Unsupported schema versions and malformed JSON produce an actionable error; loading never silently replaces a broken file. Writes use a sibling temporary file and an atomic rename when the filesystem supports it.

## Tests

The tests cover user validation and email normalization, JSON round-tripping and corrupt-file preservation, plus a create/list workflow routed through the real PureMVC Facade, Controller, Command, Proxy, and Mediator using an in-memory repository and fake screen.