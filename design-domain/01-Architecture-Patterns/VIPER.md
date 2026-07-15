# VIPER

> View-Interactor-Presenter-Entity-Router architecture for complex iOS apps.

## Overview
VIPER provides strict separation with single-responsibility components, ideal for large teams and complex features.

## Components

```
┌──────────┐     ┌───────────┐     ┌────────────┐
│   View   │◄────│ Presenter │◄────│ Interactor │
└──────────┘     └───────────┘     └────────────┘
                       │                  │
                       ▼                  ▼
                 ┌──────────┐       ┌──────────┐
                 │  Router  │       │  Entity  │
                 └──────────┘       └──────────┘
```

### View
- Passive display layer
- Sends user actions to Presenter
- UIKit or SwiftUI

### Interactor
- Business logic
- Data fetching
- Use case execution

### Presenter
- Presentation logic
- Formats data for View
- Handles View events

### Entity
- Plain data models
- No behavior

### Router
- Navigation logic
- Module assembly

## SwiftUI Adaptation

```swift
// Entity
struct Task: Identifiable {
    let id: UUID
    var title: String
    var isComplete: Bool
}

// Interactor Protocol
protocol TaskInteractorProtocol {
    func fetchTasks() async -> [Task]
    func toggleComplete(_ task: Task) async
}

// Interactor
final class TaskInteractor: TaskInteractorProtocol {
    private let repository: TaskRepository
    
    func fetchTasks() async -> [Task] {
        await repository.getAll()
    }
}

// Presenter
@Observable
final class TaskPresenter {
    private(set) var tasks: [Task] = []
    
    private let interactor: TaskInteractorProtocol
    private let router: TaskRouter
    
    func onAppear() async {
        tasks = await interactor.fetchTasks()
    }
    
    func onTaskTapped(_ task: Task) {
        router.navigateToDetail(task)
    }
}

// Router
final class TaskRouter {
    weak var viewController: UIViewController?
    
    func navigateToDetail(_ task: Task) {
        // Navigation logic
    }
    
    static func createModule() -> some View {
        let interactor = TaskInteractor(repository: TaskRepositoryImpl())
        let router = TaskRouter()
        let presenter = TaskPresenter(interactor: interactor, router: router)
        return TaskListView(presenter: presenter)
    }
}

// View
struct TaskListView: View {
    let presenter: TaskPresenter
    
    var body: some View {
        List(presenter.tasks) { task in
            TaskRow(task: task)
                .onTapGesture { presenter.onTaskTapped(task) }
        }
        .task { await presenter.onAppear() }
    }
}
```

## Pros & Cons

| Pros | Cons |
|------|------|
| Clear responsibilities | Heavy boilerplate |
| Highly testable | Overkill for simple screens |
| Scalable for teams | Steep learning curve |
| Enforced boundaries | Less natural for SwiftUI |

## When to Use
- Large enterprise apps
- Multiple developers per feature
- Complex navigation flows
- When UIKit is primary

## Related Patterns
- [[Clean-Architecture]] - Similar layering philosophy
- [[Coordinator-Pattern]] - Router equivalent
- [[MVVM]] - Simpler alternative

## Tags
#architecture #viper #enterprise #uikit #modular
