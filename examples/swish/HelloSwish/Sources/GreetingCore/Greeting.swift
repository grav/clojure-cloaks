import Foundation
import SwishKit

public final class Greeting {
    private let swish = Swish()

    public init() throws {
        let url = Bundle.module.url(forResource: "greeting", withExtension: "swish")!
        try swish.load(filename: url.path)
    }

    public func greet(_ name: String) throws -> String {
        // Encode data as a string literal; user input must never become program text.
        let encoder = JSONEncoder()
        encoder.outputFormatting = [.withoutEscapingSlashes]
        let literal = String(decoding: try encoder.encode(name), as: UTF8.self)
        let result = try swish.eval("(hello \(literal))")
        guard case .string(let text) = result else {
            throw NSError(domain: "HelloSwish", code: 1,
                          userInfo: [NSLocalizedDescriptionKey: "The script must return a string."])
        }
        return text
    }
}
