#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/HelloSwish"
: "${SIMULATOR_ID:?Set SIMULATOR_ID to an available iPhone simulator UDID from xcrun simctl list devices}"
sdk=$(xcrun --sdk iphonesimulator --show-sdk-path)
triple=arm64-apple-ios18.0-simulator
swift build --sdk "$sdk" --triple "$triple" --product HelloSwish
bin=$(swift build --sdk "$sdk" --triple "$triple" --show-bin-path)
app="$PWD/.build/HelloSwish.app"
mkdir -p "$app"
cp "$bin/HelloSwish" "$app/HelloSwish"
for bundle in "$bin/"*.bundle; do cp -R "$bundle" "$app/"; done
cat > "$app/Info.plist" <<'PLIST'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0"><dict>
<key>CFBundleIdentifier</key><string>org.clojuredialects.HelloSwish</string>
<key>CFBundleExecutable</key><string>HelloSwish</string>
<key>CFBundleName</key><string>Hello Swish</string>
<key>CFBundlePackageType</key><string>APPL</string>
<key>CFBundleVersion</key><string>1</string>
<key>CFBundleShortVersionString</key><string>1.0</string>
<key>MinimumOSVersion</key><string>18.0</string>
<key>LSRequiresIPhoneOS</key><true/>
<key>UIDeviceFamily</key><array><integer>1</integer><integer>2</integer></array>
<key>UILaunchScreen</key><dict/>
</dict></plist>
PLIST
codesign --force --sign - "$app"
xcrun simctl bootstatus "$SIMULATOR_ID" -b
xcrun simctl install "$SIMULATOR_ID" "$app"
xcrun simctl launch "$SIMULATOR_ID" org.clojuredialects.HelloSwish
