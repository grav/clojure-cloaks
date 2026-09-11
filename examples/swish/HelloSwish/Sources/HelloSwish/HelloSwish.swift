import SwiftUI
import GreetingCore

@main
struct HelloSwishApp: App {
    var body: some Scene {
        WindowGroup { GreetingView() }
    }
}

struct GreetingView: View {
    @State private var name = ""
    @State private var greeting = ""
    @State private var error: String?
    @State private var engine: Greeting?

    var body: some View {
        VStack(alignment: .leading, spacing: 24) {
            Label("SWISH × SWIFTUI", systemImage: "sparkles")
                .font(.caption.weight(.bold)).foregroundStyle(.secondary)
            Text("A little hello.").font(.largeTitle.bold())
            Text("Your name travels into a Swish function and comes back as a greeting.")
                .foregroundStyle(.secondary)
            TextField("Your name", text: $name)
                .textFieldStyle(.roundedBorder).accessibilityIdentifier("name")
                .onSubmit { greet() }
            Button("Say hello", action: greet)
                .buttonStyle(.borderedProminent).accessibilityIdentifier("greet")
            Text(greeting).font(.title2.bold()).accessibilityIdentifier("greeting")
            if let error { Text(error).foregroundStyle(.red) }
            Spacer()
            Text("Native views. A scripted greeting.").font(.footnote).foregroundStyle(.secondary)
        }
        .padding(32).frame(maxWidth: 560, alignment: .leading)
        .tint(.teal)
        .task {
            do { engine = try Greeting(); greet() }
            catch { self.error = error.localizedDescription }
        }
    }

    private func greet() {
        do { greeting = try engine?.greet(name) ?? ""; error = nil }
        catch { self.error = error.localizedDescription }
    }
}
