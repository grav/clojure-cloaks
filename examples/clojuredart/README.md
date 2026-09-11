# ClojureDart: Hello, everywhere

One Flutter app for **iOS, web, and native Linux**. Enter a name and press
**Say hello** (or submit from the keyboard). An atom holds the greeting and
submission count; `cljd.flutter` watches it and rebuilds the screen. A managed
`TextEditingController` follows the widget lifecycle. Blank names greet the world.
The bundled [Noto Emoji font](https://github.com/google/fonts/tree/main/ofl/notoemoji)
provides a consistent fallback for emoji; its [OFL license](assets/fonts/OFL.txt)
is included.

The application is [one ClojureDart namespace](src/hello/main.cljd). Flutter's
generated platform runners host it; the UI and state logic are ClojureDart.

| Target | Verified on 11 September 2026 | Preview |
| --- | --- | --- |
| Web | Release build; Chromium form input, Unicode, trimming, blank fallback, keyboard submission and counter checks | [Screenshot](screenshot-web.png) |
| Linux | Native AArch64 release executable and GTK window under Xvfb; native integration tests | [Screenshot](screenshot-linux.png) |
| iOS | iPhone 17 simulator, iOS 26.5; build, install, launch and integration tests on the Mac host | [Screenshot](screenshot-ios.png) |

The iOS check uses a simulator, not a physical device or an App Store build.
Linux runs native ARM64 code; Docker supplies the compiler and GTK libraries.

## Toolchains

Tested with Flutter **3.47.3**, Dart **3.13.3**, and ClojureDart
**0.9.20260822a** (pinned in [deps.edn](deps.edn)). Flutter's tested commit is
`e8113bf45620cbeb8aff64947ee4c93e16adb4cf`.
Install the [Clojure CLI](https://clojure.org/guides/install_clojure) and
[Flutter](https://docs.flutter.dev/install), and put both on `PATH`.

The toolchains installed for this workspace are:

- VM: `../../.cache/flutter` from this directory.
- Mac: `~/clojure-dialects-demos/toolchains/flutter`.

Compile the ClojureDart source before invoking Flutter:

```sh
cd examples/clojuredart
# This workspace's Linux SDK; omit if Flutter is already on PATH.
export PATH="$PWD/../../.cache/flutter/bin:$PATH"
clojure -M:cljd compile
flutter test
```

## Web

```sh
flutter build web --release --no-web-resources-cdn
python3 -m http.server 8074 --bind 127.0.0.1 --directory build/web
```

Open <http://127.0.0.1:8074>. The build includes CanvasKit locally. To repeat the
browser checks (stop that server first):

```sh
npm ci
npx playwright install chromium
npm run test:web
```

The browser test activates Flutter's accessibility semantics and uses its
textbox/button controls. It waits for animation frames when editing input,
because Flutter synchronizes its accessible DOM with the rendered widget tree.

## Native Linux

With Flutter's Linux desktop dependencies installed:

```sh
flutter run -d linux
flutter build linux --release
```

On this ARM64 VM, the [container wrapper](linux-container.sh) installs the Linux
build dependencies without changing host packages. It uses the workspace SDK,
or the directory specified by `FLUTTER_SDK`:

```sh
./linux-container.sh flutter build linux --release
./linux-container.sh xvfb-run -a flutter test integration_test/app_test.dart -d linux
./linux-container.sh xvfb-run -a ./capture-linux.sh
```

The release application is `build/linux/arm64/release/bundle/cljd_clojuredart`;
keep its `data/` and `lib/` directories alongside it. The wrapper mounts the
project and caches and restores ownership of generated build files on exit.
Xvfb supplies a virtual X11 screen for this VM's native graphical checks.

## iOS

On a Mac with Xcode, copy this project, add Flutter and Clojure to `PATH`, then:

```sh
clojure -M:cljd compile
flutter build ios --simulator --debug
flutter devices
flutter run -d <simulator-id>
flutter test integration_test/app_test.dart -d <simulator-id>
```

The verified copy is on the Mac host at `~/clojure-dialects-demos/clojuredart`.
Its dedicated simulator is `HelloClojureDartDemo`
(`839BA4A4-837D-400C-95E1-D4D54F62CCED`). The application remains installed there.
The integration tests use Flutter's text-input test channel for Unicode input
while exercising the actual native app, widgets and atom updates.

For ClojureDart hot reload, use `clojure -M:cljd flutter` and select a device.
See the [upstream Flutter quick start](https://github.com/Tensegritics/ClojureDart/blob/main/doc/flutter-quick-start.md).

![Hello, everywhere on the web](screenshot-web.png)
