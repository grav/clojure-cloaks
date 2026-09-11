import 'package:integration_test/integration_test.dart';
import '../test/widget_test.dart' as shared;

void main() {
  final binding = IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  // Use Flutter's text-input channel for deterministic Unicode input on devices.
  binding.testTextInput.register();
  shared.greetingTests();
}
