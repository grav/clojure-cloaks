import Foundation
import SwishKit

public struct Element: Identifiable {
    public let kind: String
    public let id: String
    public let text: String
}

public final class Greeting {
    private let swish = Swish()

    public init() throws {
        let url = Bundle.module.url(forResource: "greeting", withExtension: "swish")!
        try swish.load(filename: url.path)
    }

    public func screen() throws -> [Element] {
        guard case .vector(let nodes, _) = try swish.eval("(screen)") else {
            throw invalidScreen()
        }
        return try nodes.elements.map { node in
            guard case .vector(let parts, _) = node, parts.count == 3,
                  case .keyword(let kind) = parts[0],
                  ["text", "field", "button"].contains(kind),
                  case .keyword(let id) = parts[1],
                  case .string(let text) = parts[2] else { throw invalidScreen() }
            return Element(kind: kind, id: id, text: text)
        }
    }

    public func send(_ event: String, value: String = "") throws {
        // Both strings are encoded as data, including the event identifier.
        let encoder = JSONEncoder()
        encoder.outputFormatting = [.withoutEscapingSlashes]
        func literal(_ text: String) throws -> String {
            String(decoding: try encoder.encode(text), as: UTF8.self)
        }
        _ = try swish.eval("(dispatch! (keyword \(try literal(event))) \(try literal(value)))")
    }

    private func invalidScreen() -> NSError {
        NSError(domain: "HelloSwish", code: 1,
                userInfo: [NSLocalizedDescriptionKey: "Expected [kind id text] controls from Swish."])
    }
}
