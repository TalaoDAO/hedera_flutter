import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'hedera_flutter_method_channel.dart';

abstract class HederaFlutterPlatform extends PlatformInterface {
  /// Constructs a HederaFlutterPlatform.
  HederaFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static HederaFlutterPlatform _instance = MethodChannelHederaFlutter();

  /// The default instance of [HederaFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelHederaFlutter].
  static HederaFlutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [HederaFlutterPlatform] when
  /// they register themselves.
  static set instance(HederaFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<Map> createAccount({
    required String accountId,
    required String privateKey,
  }) async {
    throw UnimplementedError('createAccount() has not been implemented.');
  }

  Future<Map> transferCrypto({
    required String accountId,
    required String privateKey,
  }) async {
    throw UnimplementedError('transferCrypto() has not been implemented.');
  }
}
