import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import '../lib/main.dart' as app;

// Shared with the real Linux/iOS integration runner.
void greetingTests() {
  testWidgets('greetings preserve Unicode, trim input, and count submissions', (tester) async {
    app.main();
    await tester.pumpAndSettle();
    expect(find.text('Hello, world!'), findsOneWidget);
    expect(find.text('Greetings sent: 0'), findsOneWidget);
    final name = find.byKey(const ValueKey('name'));
    final greet = find.byKey(const ValueKey('greet'));
    await tester.enterText(name, '  Björk 🌍  ');
    await tester.pumpAndSettle();
    await tester.tap(greet);
    await tester.pumpAndSettle();
    expect(find.text('Hello, Björk 🌍!'), findsOneWidget);
    expect(find.text('Greetings sent: 1'), findsOneWidget);
    await tester.enterText(name, '   ');
    await tester.pumpAndSettle();
    await tester.tap(greet);
    await tester.pumpAndSettle();
    expect(tester.widget<Text>(find.byKey(const ValueKey('greeting'))).data, 'Hello, world!');
    expect(find.text('Greetings sent: 2'), findsOneWidget);
    expect(find.text('Hello, Björk 🌍!'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('history keeps duplicates, puts newest first, and scrolls', (tester) async {
    app.main();
    await tester.pumpAndSettle();
    final name = find.byKey(const ValueKey('name'));
    final greet = find.byKey(const ValueKey('greet'));
    for (var i = 0; i < 12; i++) {
      await tester.enterText(name, i < 2 ? 'Ada' : 'Guest $i');
      await tester.ensureVisible(greet);
      await tester.tap(greet);
      await tester.pumpAndSettle();
    }
    expect(tester.widget<Text>(find.byKey(const ValueKey('greeting'))).data,
        'Hello, Guest 11!');
    expect(tester.widget<Text>(find.byKey(const ValueKey('greeting-1'))).data,
        'Hello, Guest 10!');
    expect(find.text('Greetings sent: 12'), findsOneWidget);
    final history = find.byKey(const ValueKey('greetings'));
    await tester.ensureVisible(history);
    await tester.pumpAndSettle();
    final scrollable = find.descendant(of: history, matching: find.byType(Scrollable));
    final position = tester.state<ScrollableState>(scrollable).position;
    expect(position.maxScrollExtent, greaterThan(0));
    await tester.drag(history, const Offset(0, -1000));
    await tester.pumpAndSettle();
    expect(position.pixels, greaterThan(0));
    expect(find.text('Hello, Ada!'), findsNWidgets(2));
    expect(tester.takeException(), isNull);
  });

}

void main() => greetingTests();
