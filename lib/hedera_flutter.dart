import 'hedera_flutter_platform_interface.dart';

/// Provide Hedera API for both Android and iOS.
class HederaFlutter {
  /// create Hedera Account
  Future<Map> createAccount({
    required String accountId,
    required String privateKey,
  }) async {
    return await HederaFlutterPlatform.instance.createAccount(
      accountId: accountId,
      privateKey: privateKey,
    );
  }

  /// create Hedera Account With Alias
  Future<Map> createAccountWithAlias({
    required String accountId,
    required String privateKey,
  }) async {
    return await HederaFlutterPlatform.instance.createAccountWithAlias(
      accountId: accountId,
      privateKey: privateKey,
    );
  }

  /// transfer Crypto
  Future<Map> transferCrypto({
    required String accountId,
    required String privateKey,
  }) async {
    return await HederaFlutterPlatform.instance.transferCrypto(
      accountId: accountId,
      privateKey: privateKey,
    );
  }
}
