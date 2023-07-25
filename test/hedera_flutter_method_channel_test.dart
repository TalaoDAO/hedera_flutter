import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:hedera_flutter/hedera_flutter_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelHederaFlutter platform = MethodChannelHederaFlutter();
  const MethodChannel channel = MethodChannel('hederaMethod');
}
