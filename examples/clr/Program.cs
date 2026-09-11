using Clj = clojure.clr.api.Clojure;

// Load source through the public API, then pass CLR objects directly to a Var.
Clj.var("clojure.core", "load-file").invoke(Path.Combine(AppContext.BaseDirectory, "hello.clj"));
var greeting = Clj.var("hello.clr", "greeting");
var receipt = Clj.var("hello.clr", "receipt");
if (args.Length == 1 && args[0] == "--check")
{
    foreach (var name in new[] { "", " ", "Ada", "Ægir", "\") (throw 1) ;", "a\nb" })
    {
        var expected = $"Hello, {(string.IsNullOrWhiteSpace(name) ? "world" : name)} from ClojureCLR!";
        var actual = (string)greeting.invoke(name);
        if (actual != expected) throw new Exception($"Greeting mismatch: {actual}");
    }
    var stamped = (string)receipt.invoke("Ada");
    var timestamp = stamped.Split(" UTC: ")[1];
    if (!DateTimeOffset.TryParse(timestamp, out var parsed) || parsed.Offset != TimeSpan.Zero)
        throw new Exception("The ClojureCLR function did not return a UTC timestamp.");
    Console.WriteLine(stamped);
    Console.WriteLine("C# → ClojureCLR → .NET interop checks passed.");
}
else
{
    Console.WriteLine(receipt.invoke(args.Length == 0 ? "world" : string.Join(" ", args)));
}
