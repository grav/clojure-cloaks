import XCTest
@testable import GreetingCore

final class GreetingTests: XCTestCase {
    private func text(_ id: String, in engine: Greeting) throws -> String? {
        try engine.screen().first { $0.id == id }?.text
    }

    func testScreenAndEvents() throws {
        let engine = try Greeting()
        XCTAssertEqual(try engine.screen().map(\.kind),
                       ["text", "field", "button", "text", "text", "button"])
        XCTAssertEqual(try text("greeting", in: engine), "Hello, world!")
        XCTAssertEqual(try text("count", in: engine), "Greetings: 0")
        try engine.send("name", value: "Ada")
        XCTAssertEqual(try text("name", in: engine), "Ada")
        XCTAssertEqual(try text("greeting", in: engine), "Hello, world!")
        try engine.send("greet")
        try engine.send("greet")
        XCTAssertEqual(try text("greeting", in: engine), "Hello, Ada!")
        XCTAssertEqual(try text("count", in: engine), "Greetings: 2")
        try engine.send("reset")
        XCTAssertEqual(try text("name", in: engine), "")
        XCTAssertEqual(try text("greeting", in: engine), "Hello, world!")
        XCTAssertEqual(try text("count", in: engine), "Greetings: 0")
    }

    func testScriptBoundary() throws {
        let engine = try Greeting()
        for name in ["", "Ada", "Ægir", "\" ) (throw 1) ;", "line\nbreak", "a/b"] {
            try engine.send("name", value: name)
            try engine.send("greet")
            XCTAssertEqual(try text("greeting", in: engine),
                           "Hello, \(name.isEmpty ? "world" : name)!")
        }
    }
}
