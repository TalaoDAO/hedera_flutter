import 'package:flutter_test/flutter_test.dart';
import 'package:hedera_flutter/hedera_flutter.dart';
import 'package:hedera_flutter/hedera_flutter_platform_interface.dart';
import 'package:hedera_flutter/hedera_flutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHederaFlutterPlatform
    with MockPlatformInterfaceMixin
    implements HederaFlutterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final HederaFlutterPlatform initialPlatform = HederaFlutterPlatform.instance;

  test('$MethodChannelHederaFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHederaFlutter>());
  });

  test('getPlatformVersion', () async {
    HederaFlutter hederaFlutterPlugin = HederaFlutter();
    MockHederaFlutterPlatform fakePlatform = MockHederaFlutterPlatform();
    HederaFlutterPlatform.instance = fakePlatform;

    expect(await hederaFlutterPlugin.getPlatformVersion(), '42');
  });
}
