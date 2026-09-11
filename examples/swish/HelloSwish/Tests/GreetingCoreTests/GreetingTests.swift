import XCTest
@testable import GreetingCore

final class GreetingTests: XCTestCase {
    func testScriptBoundary() throws {
        let engine = try Greeting()
        for name in ["", "Ada", "Ægir", "\" ) (throw 1) ;", "line\nbreak", "a/b"] {
            XCTAssertEqual(try engine.greet(name), "Hello, \(name.isEmpty ? "world" : name)!")
        }
    }
}
