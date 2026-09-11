const std = @import("std");
const sci = @cImport(@cInclude("libsci.h"));
const stdio = @cImport(@cInclude("stdio.h"));

pub fn main(init: std.process.Init) !void {
    const args = try init.minimal.args.toSlice(init.arena.allocator());
    if (args.len != 2) {
        std.debug.print("Usage: sci-zig EXPRESSION\n", .{});
        return error.MissingExpression;
    }
    var isolate: ?*sci.graal_isolate_t = null;
    var thread: ?*sci.graal_isolatethread_t = null;
    if (sci.graal_create_isolate(null, &isolate, &thread) != 0)
        return error.CreateIsolate;
    defer _ = sci.graal_tear_down_isolate(thread);

    const result = sci.eval_string(@intCast(@intFromPtr(thread.?)), args[1].ptr);
    if (result == null) return error.NullResult;
    // Print while the isolate that owns the result is still alive.
    if (stdio.puts(result) < 0) return error.WriteOutput;
}
