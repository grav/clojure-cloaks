# Swish inside SwiftUI

Swish owns the application: an atom holds the name, greeting and click count;
`dispatch!` handles edits, greetings and resets; `screen` returns vectors
describing the native controls. All of that lives in
[greeting.swish](HelloSwish/Sources/GreetingCore/Resources/greeting.swish).

For example, `[:button :greet "Say hello"]` renders a native button that sends
the `:greet` event back to Swish. Swift only loads the interpreter, decodes
`[kind id text]` vectors, and renders text, fields and buttons in a SwiftUI stack.
This tiny renderer is part of the demo, not a built-in Swish UI library.
After each event it asks Swish for the screen again. User input and event names
are encoded as string literals at the interpreter boundary.

Type a name, press **Say hello**, and watch the greeting and counter change.
**Start again** resets the state. Change the script to rearrange controls or
change their behavior without editing Swift; rerun to load the bundled script.

On a Mac with Xcode and Swift 6.2 or later:

```sh
cd examples/swish/HelloSwish
swift test
swift run HelloSwish
```

For an Apple Silicon iOS simulator, from `examples/swish`:

```sh
xcrun simctl list devices available
SIMULATOR_ID=<your-iphone-simulator-udid> ./run-simulator.sh
open -a Simulator
```

The script builds for the ARM64 simulator, assembles an app with its resource
bundles, signs it locally, boots the selected simulator, installs and launches.
No developer account or Xcode project generator is required. It does not build
for a physical iPhone or distribute the app.

Verified 2026-09-11 on an Apple Silicon Mac reached from the Linux VM over SSH:

- macOS build and XCTest checks for the screen, editing, greeting count, reset, and default, named, Unicode, quoted, newline and slash input passed.
- iOS 26.5 simulator build, install and launch passed; the rendered screen displayed the greeting produced by Swish.
- Screenshot inspected; simulator typing/button interaction has not been automated.

![Swish running in the iPhone simulator](screenshot.png)

Swish is pinned to commit
[`05f0de5`](https://github.com/infiniteNIL/swish/tree/05f0de5fe7755ec935dcd693dfb4c333c4cc0c33),
which supports Swift 6.2. Newer upstream commits require Swift 6.4; the tested
Xcode 26.5 installation supplies Swift 6.3.2. `Package.resolved` locks transitive
dependencies. Keep the `HelloSwish` package subdirectory: naming the root package
folder `swish` collides with the dependency's SwiftPM identity.
