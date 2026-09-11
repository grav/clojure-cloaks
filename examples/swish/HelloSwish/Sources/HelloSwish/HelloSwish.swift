import SwiftUI
import GreetingCore

@main
struct HelloSwishApp: App {
    var body: some Scene {
        WindowGroup { ScriptView() }
    }
}

// Swish owns the screen and events; this host renders three native controls.
struct ScriptView: View {
    @State private var elements: [Element] = []
    @State private var error: String?
    @State private var engine: Greeting?

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            ForEach(elements) { element in
                switch element.kind {
                case "field":
                    TextField(element.id.capitalized, text: Binding(
                        get: { element.text },
                        set: { send(element.id, value: $0) }
                    ))
                    .textFieldStyle(.roundedBorder)
                    .accessibilityIdentifier(element.id)
                case "button":
                    Button(element.text) { send(element.id) }
                        .buttonStyle(.borderedProminent)
                        .accessibilityIdentifier(element.id)
                default:
                    Text(element.text).accessibilityIdentifier(element.id)
                }
            }
            if let error { Text(error).foregroundStyle(.red) }
        }
        .padding(32).frame(maxWidth: 560, alignment: .leading)
        .task {
            do {
                let engine = try Greeting()
                self.engine = engine
                elements = try engine.screen()
            } catch { self.error = error.localizedDescription }
        }
    }

    private func send(_ event: String, value: String = "") {
        do {
            guard let engine else { return }
            try engine.send(event, value: value)
            elements = try engine.screen()
            error = nil
        } catch { self.error = error.localizedDescription }
    }
}
