# View Composition

> Building complex UIs from simple, reusable SwiftUI components.

## Overview
SwiftUI's declarative nature encourages composing small, focused views into larger interfaces, improving readability and reusability.

## Principles

### 1. Single Responsibility Views
Each view does one thing well.

```swift
// ❌ Monolithic view
struct UserProfileView: View {
    var body: some View {
        VStack {
            // 200 lines of avatar, stats, posts, settings...
        }
    }
}

// ✅ Composed views
struct UserProfileView: View {
    let user: User
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                ProfileHeader(user: user)
                StatsSection(stats: user.stats)
                PostsGrid(posts: user.posts)
            }
        }
    }
}

struct ProfileHeader: View {
    let user: User
    
    var body: some View {
        HStack {
            AvatarView(url: user.avatarURL, size: 80)
            VStack(alignment: .leading) {
                Text(user.name).font(.title2)
                Text("@\(user.username)").foregroundStyle(.secondary)
            }
        }
    }
}
```

### 2. Extract Subviews

```swift
struct OrderListView: View {
    let orders: [Order]
    
    var body: some View {
        List(orders) { order in
            OrderRow(order: order)
        }
    }
}

struct OrderRow: View {
    let order: Order
    
    var body: some View {
        HStack {
            orderIcon
            orderDetails
            Spacer()
            orderStatus
        }
    }
    
    private var orderIcon: some View {
        Image(systemName: "shippingbox")
            .foregroundStyle(.blue)
    }
    
    private var orderDetails: some View {
        VStack(alignment: .leading) {
            Text(order.title)
            Text(order.date, style: .date)
                .font(.caption)
                .foregroundStyle(.secondary)
        }
    }
    
    private var orderStatus: some View {
        Text(order.status.rawValue)
            .font(.caption)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(order.status.color.opacity(0.2))
            .clipShape(Capsule())
    }
}
```

### 3. Generic Reusable Views

```swift
struct CardView<Content: View>: View {
    let content: Content
    
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }
    
    var body: some View {
        content
            .padding()
            .background(.background)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .shadow(radius: 2)
    }
}

// Usage
CardView {
    VStack {
        Text("Title")
        Text("Subtitle")
    }
}
```

### 4. Configurable Components

```swift
struct PrimaryButton: View {
    let title: String
    let icon: String?
    let action: () -> Void
    
    init(_ title: String, icon: String? = nil, action: @escaping () -> Void) {
        self.title = title
        self.icon = icon
        self.action = action
    }
    
    var body: some View {
        Button(action: action) {
            HStack {
                if let icon {
                    Image(systemName: icon)
                }
                Text(title)
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(.blue)
            .foregroundStyle(.white)
            .clipShape(RoundedRectangle(cornerRadius: 10))
        }
    }
}
```

## Composition Patterns

### Container + Content
```swift
struct PageContainer<Content: View>: View {
    let title: String
    @ViewBuilder let content: Content
    
    var body: some View {
        NavigationStack {
            ScrollView {
                content
                    .padding()
            }
            .navigationTitle(title)
        }
    }
}
```

### Slot Pattern
```swift
struct ListCard<Header: View, Content: View, Footer: View>: View {
    @ViewBuilder let header: Header
    @ViewBuilder let content: Content
    @ViewBuilder let footer: Footer
    
    var body: some View {
        VStack(alignment: .leading) {
            header
            Divider()
            content
            Divider()
            footer
        }
    }
}
```

## Related Patterns
- [[ViewModifier-Pattern]] - Behavior composition
- [[Container-Presentational]] - Logic separation
- [[State-and-Binding]] - Data flow

## Tags
#ui-patterns #swiftui #composition #reusability #components
