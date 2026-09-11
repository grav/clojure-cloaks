const std = @import("std");
const c = @cImport({
    @cInclude("libsci.h");
    @cInclude("stdio.h");
});

pub fn main(init: std.process.Init) !void {
    const args = try init.minimal.args.toSlice(init.arena.allocator());
    if (args.len != 2) {
        std.debug.print("Usage: sci-zig EXPRESSION\n", .{});
        return error.MissingExpression;
    }
    var isolate: ?*c.graal_isolate_t = null;
    var thread: ?*c.graal_isolatethread_t = null;
    if (c.graal_create_isolate(null, &isolate, &thread) != 0)
        return error.CreateIsolate;
    defer _ = c.graal_tear_down_isolate(thread);

    const result = c.eval_string(@intCast(@intFromPtr(thread.?)), args[1].ptr);
    if (result == null) return error.NullResult;
    // Print while the isolate that owns the result is still alive.
    if (c.puts(result) < 0) return error.WriteOutput;
}
