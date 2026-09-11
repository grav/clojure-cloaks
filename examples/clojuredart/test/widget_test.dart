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
    expect(tester.takeException(), isNull);
  });
}

void main() => greetingTests();
