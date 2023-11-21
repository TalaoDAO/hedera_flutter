import 'hedera_flutter_platform_interface.dart';

/// Provide Hedera API for both Android and iOS.
class HederaFlutter {
  /// create Hedera Account
  Future<Map> createAccount({
    required String accountId,
    required String privateKey,
    required String network,
  }) async {
    return await HederaFlutterPlatform.instance.createAccount(
      accountId: accountId,
      privateKey: privateKey,
      network: network,
    );
  }

  /// create Hedera Account With Alias
  Future<Map> createAccountWithAlias({
    required String accountId,
    required String privateKey,
    required String network,
  }) async {
    return await HederaFlutterPlatform.instance.createAccountWithAlias(
      accountId: accountId,
      privateKey: privateKey,
      network: network,
    );
  }

  /// create Hedera Account With Alias
  Future<Map> createAccountWithMnemonics({
    required String accountId,
    required String privateKey,
    required String network,
    required String mnemonicsString,
  }) async {
    return await HederaFlutterPlatform.instance.createAccountWithMnemonics(
      accountId: accountId,
      privateKey: privateKey,
      network: network,
      mnemonicsString: mnemonicsString,
    );
  }

  /// get balance
  Future<Map> queryBalance({
    required String accountId,
    required String network,
  }) async {
    return await HederaFlutterPlatform.instance.queryBalance(
      accountId: accountId,
      network: network,
    );
  }

  /// transfer Hbar
  Future<Map> transferHbar({
    required String accountId,
    required String privateKey,
    required String network,
  }) async {
    return await HederaFlutterPlatform.instance.transferHbar(
      accountId: accountId,
      privateKey: privateKey,
      network: network,
    );
  }
}
