# ViewModifier Pattern

> Encapsulating and reusing view modifications in SwiftUI.

## Overview
ViewModifiers allow extracting common view styling and behavior into reusable components, applied via the `.modifier()` method or custom extensions.

## Basic ViewModifier

```swift
struct CardStyle: ViewModifier {
    func body(content: Content) -> some View {
        content
            .padding()
            .background(.background)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

// Usage
Text("Hello")
    .modifier(CardStyle())

// Better: Extension
extension View {
    func cardStyle() -> some View {
        modifier(CardStyle())
    }
}

Text("Hello")
    .cardStyle()
```

## Parameterized Modifiers

```swift
struct RoundedBorder: ViewModifier {
    let color: Color
    let lineWidth: CGFloat
    let cornerRadius: CGFloat
    
    init(color: Color = .gray, lineWidth: CGFloat = 1, cornerRadius: CGFloat = 8) {
        self.color = color
        self.lineWidth = lineWidth
        self.cornerRadius = cornerRadius
    }
    
    func body(content: Content) -> some View {
        content
            .overlay(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(color, lineWidth: lineWidth)
            )
    }
}

extension View {
    func roundedBorder(
        color: Color = .gray,
        lineWidth: CGFloat = 1,
        cornerRadius: CGFloat = 8
    ) -> some View {
        modifier(RoundedBorder(color: color, lineWidth: lineWidth, cornerRadius: cornerRadius))
    }
}

// Usage
TextField("Email", text: $email)
    .roundedBorder(color: .blue, cornerRadius: 10)
```

## Conditional Modifiers

```swift
struct ConditionalModifier<M: ViewModifier>: ViewModifier {
    let condition: Bool
    let modifier: M
    
    func body(content: Content) -> some View {
        if condition {
            content.modifier(modifier)
        } else {
            content
        }
    }
}

extension View {
    @ViewBuilder
    func `if`<Content: View>(_ condition: Bool, transform: (Self) -> Content) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }
}

// Usage
Text("Highlighted")
    .if(isHighlighted) { view in
        view.foregroundStyle(.yellow)
    }
```

## Stateful Modifiers

```swift
struct ShakeEffect: ViewModifier {
    @State private var shake = false
    let trigger: Bool
    
    func body(content: Content) -> some View {
        content
            .offset(x: shake ? -10 : 0)
            .animation(.default.repeatCount(3).speed(6), value: shake)
            .onChange(of: trigger) {
                shake = true
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                    shake = false
                }
            }
    }
}

extension View {
    func shake(trigger: Bool) -> some View {
        modifier(ShakeEffect(trigger: trigger))
    }
}
```

## Button Styles (Related Pattern)

```swift
struct PrimaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding()
            .frame(maxWidth: .infinity)
            .background(configuration.isPressed ? Color.blue.opacity(0.8) : Color.blue)
            .foregroundStyle(.white)
            .clipShape(RoundedRectangle(cornerRadius: 10))
            .scaleEffect(configuration.isPressed ? 0.98 : 1)
    }
}

extension ButtonStyle where Self == PrimaryButtonStyle {
    static var primary: PrimaryButtonStyle { PrimaryButtonStyle() }
}

// Usage
Button("Submit") { }
    .buttonStyle(.primary)
```

## Text Field Styles

```swift
struct RoundedTextFieldStyle: TextFieldStyle {
    func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .padding()
            .background(.gray.opacity(0.1))
            .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}
```

## Composing Modifiers

```swift
struct FormFieldStyle: ViewModifier {
    func body(content: Content) -> some View {
        content
            .textFieldStyle(.roundedBorder)
            .autocorrectionDisabled()
            .textInputAutocapitalization(.never)
    }
}

struct FormField<Content: View>: View {
    let label: String
    @ViewBuilder let content: Content
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.caption)
                .foregroundStyle(.secondary)
            content
                .modifier(FormFieldStyle())
        }
    }
}
```

## Related Patterns
- [[View-Composition]] - Building with modifiers
- [[Container-Presentational]] - Style vs content
- [[State-and-Binding]] - Stateful modifiers

## Tags
#ui-patterns #swiftui #modifiers #styling #reusability
