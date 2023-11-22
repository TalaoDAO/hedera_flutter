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
    required String network,
  }) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("accountId", () => accountId);
    args.putIfAbsent("privateKey", () => privateKey);
    args.putIfAbsent("network", () => network);
    final data = await methodChannel.invokeMethod('createAccount', args);

    Map<String, dynamic> resultMap = data is String ? json.decode(data) : data;
    return resultMap;
  }

  /// create Hedera Account With Alias
  @override
  Future<Map> createAccountWithAlias({
    required String accountId,
    required String privateKey,
    required String network,
  }) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("accountId", () => accountId);
    args.putIfAbsent("privateKey", () => privateKey);
    args.putIfAbsent("network", () => network);
    final data =
        await methodChannel.invokeMethod('createAccountWithAlias', args);

    Map<String, dynamic> resultMap = data is String ? json.decode(data) : data;
    return resultMap;
  }

  /// create Hedera Account With Mnemonics
  @override
  Future<Map> createAccountWithMnemonics({
    required String accountId,
    required String privateKey,
    required String network,
    required String mnemonicsString,
  }) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("accountId", () => accountId);
    args.putIfAbsent("privateKey", () => privateKey);
    args.putIfAbsent("network", () => network);
    args.putIfAbsent("mnemonicsString", () => mnemonicsString);
    final data =
        await methodChannel.invokeMethod('createAccountWithMnemonics', args);

    Map<String, dynamic> resultMap = data is String ? json.decode(data) : data;
    return resultMap;
  }

  /// get balance
  @override
  Future<Map> queryBalance({
    required String accountId,
    required String network,
  }) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("accountId", () => accountId);
    args.putIfAbsent("network", () => network);
    final data = await methodChannel.invokeMethod('queryBalance', args);

    Map<String, dynamic> resultMap = data is String ? json.decode(data) : data;
    return resultMap;
  }

  /// transfer Hbar
  @override
  Future<Map> transferHbar({
    required String accountId,
    required String privateKey,
    required String network,
  }) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("accountId", () => accountId);
    args.putIfAbsent("privateKey", () => privateKey);
    args.putIfAbsent("network", () => network);
    final data = await methodChannel.invokeMethod('transferHbar', args);

    Map<String, dynamic> resultMap = data is String ? json.decode(data) : data;
    return resultMap;
  }

  /// transfer token
  @override
  Future<Map> transferToken({
    required String accountId,
    required String privateKey,
    required String network,
  }) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("accountId", () => accountId);
    args.putIfAbsent("privateKey", () => privateKey);
    args.putIfAbsent("network", () => network);
    final data = await methodChannel.invokeMethod('transferToken', args);

    Map<String, dynamic> resultMap = data is String ? json.decode(data) : data;
    return resultMap;
  }
}
