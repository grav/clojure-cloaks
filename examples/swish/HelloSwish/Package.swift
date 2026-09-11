// swift-tools-version: 6.2
import PackageDescription
let package = Package(
    name: "HelloSwish",
    platforms: [.macOS(.v15), .iOS(.v18)],
    products: [.executable(name: "HelloSwish", targets: ["HelloSwish"])],
    dependencies: [.package(url: "https://github.com/infiniteNIL/swish.git", revision: "05f0de5fe7755ec935dcd693dfb4c333c4cc0c33")],
    targets: [
        .target(name: "GreetingCore", dependencies: [.product(name: "SwishKit", package: "swish")], resources: [.copy("Resources/greeting.swish")]),
        .executableTarget(name: "HelloSwish", dependencies: ["GreetingCore"]),
        .testTarget(name: "GreetingCoreTests", dependencies: ["GreetingCore"])
    ]
)
