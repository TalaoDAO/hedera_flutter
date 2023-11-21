package io.altme.hedera_flutter.hedera_flutter;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * HederaFlutterPlugin
 */
public class HederaFlutterPlugin implements FlutterPlugin, MethodCallHandler {

    private MethodChannel methodChannel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "hederaMethod");
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "createAccount" -> {
                final String accountId = call.argument("accountId");
                final String privateKey = call.argument("privateKey");
                final String network = call.argument("network");
                AccountService.createAccount(accountId, privateKey, network, result);
            }
            case "createAccountWithAlias" -> {
                final String accountId = call.argument("accountId");
                final String privateKey = call.argument("privateKey");
                final String network = call.argument("network");
                AccountService.createAccountWithAlias(accountId, privateKey, network, result);
            }
            case "createAccountWithMnemonics" -> {
                final String accountId = call.argument("accountId");
                final String privateKey = call.argument("privateKey");
                final String network = call.argument("network");
                final String mnemonicsString = call.argument("mnemonicsString");
                AccountService.createAccountWithMnemonics(accountId, privateKey, network, mnemonicsString, result);
            }
            case "queryBalance" -> {
                final String accountId = call.argument("accountId");
                final String network = call.argument("network");
                BasicService.queryBalance(accountId, network, result);
            }
            case "transferHbar" -> {
                final String accountId = call.argument("accountId");
                final String privateKey = call.argument("privateKey");
                final String network = call.argument("network");
                AccountService.transferHbar(accountId, privateKey, network, result);
            }
            default -> result.notImplemented();
        }
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
    }
}
