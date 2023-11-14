package io.altme.hedera_flutter.hedera_flutter;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.Hbar;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * HederaFlutterPlugin
 */
public class HederaFlutterPlugin implements FlutterPlugin, MethodCallHandler {

    private static final String tag = "hedera";

    private MethodChannel methodChannel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "hederaMethod");
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "createAccount":
                final String accountId = call.argument("accountId");
                final String privateKey = call.argument("privateKey");
                createAccounts(accountId, privateKey, result);
                break;
            default:
                result.notImplemented();
        }
    }

    private void createAccounts(String accountId, String privateKey, Result result) {
        try {
            //Grab your Hedera Testnet account ID and private key
            AccountId myAccountId = AccountId.fromString(accountId);
            PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

            //Create your Hedera Testnet client
            Client client = Client.forName("testnet");

            // Defaults the operator account ID and key such that all generated transactions will be paid for
            // by this account and be signed by this key
            client.setOperator(myAccountId, myPrivateKey);

            // Generate a Ed25519 private, public key pair
            PrivateKey newAccountPrivateKey = PrivateKey.generateED25519();
            PublicKey newAccountPublicKey = newAccountPrivateKey.getPublicKey();


            Log.i(tag, "\nprivate ke: " + newAccountPrivateKey);
            Log.i(tag, "\npublic key: " + newAccountPublicKey);

            //Create new account and assign the public key
            TransactionResponse transactionResponse = new AccountCreateTransaction()
                    // The only _required_ property here is `key`
                    .setKey(newAccountPublicKey)
                    .setInitialBalance(Hbar.fromTinybars(1000))
                    .execute(client);

            // This will wait for the receipt to become available
            TransactionReceipt receipt = transactionResponse.getReceipt(client);

            AccountId newAccountId = receipt.accountId;

            Log.i(tag, "\nNew account ID: " + newAccountId);

             Map<String, Object> mapData = new HashMap<>();
             mapData.put("message", true);
             mapData.put("id", newAccountId);
             result.success(mapData);

        } catch (Exception e) {
            Map<String, Object> mapData = new HashMap<>();
            mapData.put("message", e.toString());
            mapData.put("success", false);
            result.success(mapData);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
    }
}
