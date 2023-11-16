import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'hedera_flutter_platform_interface.dart';

/// An implementation of [HederaFlutterPlatform] that uses method channels.
class MethodChannelHederaFlutter extends HederaFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('hederaMethod');

  /// create Hedera Account
  @override
  Future<Map> createAccount({
    required String accountId,
    required String privateKey,
  }) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("accountId", () => accountId);
    args.putIfAbsent("privateKey", () => privateKey);
    final data = await methodChannel.invokeMethod('createAccount', args);

    Map<String, dynamic> resultMap = data is String ? json.decode(data) : data;
    return resultMap;
  }

  /// transfer Crypto
  @override
  Future<Map> transferCrypto({
    required String accountId,
    required String privateKey,
  }) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("accountId", () => accountId);
    args.putIfAbsent("privateKey", () => privateKey);
    final data = await methodChannel.invokeMethod('transferCrypto', args);

    Map<String, dynamic> resultMap = data is String ? json.decode(data) : data;
    return resultMap;
  }
}
