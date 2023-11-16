package io.altme.hedera_flutter.hedera_flutter;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.TransferTransaction;

import org.json.JSONObject;

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
            case "createAccount" -> {
                final String accountId = call.argument("accountId");
                final String privateKey = call.argument("privateKey");
                createAccounts(accountId, privateKey, result);
            }
            case "transferCrypto" -> {
                final String accountId = call.argument("accountId");
                final String privateKey = call.argument("privateKey");
                transferCrypto(accountId, privateKey, result);
            }
            default -> result.notImplemented();
        }
    }

    private void createAccounts(String accountId, String privateKey, Result result) {
        try {
            //Grab your Hedera Testnet account ID and private key
            AccountId myAccountId = AccountId.fromString(accountId);
            PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

            //Create your Hedera Testnet client
            Client client = Client.forTestnet();
            //Client client = Client.forName("testnet");

            // Defaults the operator account ID and key such that all generated transactions will be paid for
            // by this account and be signed by this key
            client.setOperator(myAccountId, myPrivateKey);

            // Set default max transaction fee & max query payment
            client.setDefaultMaxTransactionFee(new Hbar(100));
            client.setMaxQueryPayment(new Hbar(50));

            // Generate a Ed25519 private, public key pair
            PrivateKey newAccountPrivateKey = PrivateKey.generateED25519();
            PublicKey newAccountPublicKey = newAccountPrivateKey.getPublicKey();

            Log.i(tag, "\nprivate key: " + newAccountPrivateKey);
            Log.i(tag, "\npublic key: " + newAccountPublicKey);

            //Create new account and assign the public key
            TransactionResponse transactionResponse = new AccountCreateTransaction()
                    // The only _required_ property here is `key`
                    .setKey(newAccountPublicKey)
                    .setInitialBalance(Hbar.fromTinybars(1000))
                    .execute(client);

            // This will wait for the receipt to become available
            TransactionReceipt receipt = transactionResponse.getReceipt(client); 

            Log.i(tag, "\nreceipt " + receipt);

            AccountId newAccountId = receipt.accountId;

            Log.i(tag, "\nNew account ID: " + newAccountId);

            //Check the new account's balance
            AccountBalance accountBalance = new AccountBalanceQuery()
                    .setAccountId(newAccountId)
                    .execute(client);

            Log.i(tag, "\nNew account balance: " + accountBalance);

            Map<String, Object> mapData = new HashMap<>();
            mapData.put("success", true);
            mapData.put("id", newAccountId.toString());
            mapData.put("balance", accountBalance.hbars.toString());
            Log.i(tag, "\nMap: " + mapData);
            JSONObject json = new JSONObject(mapData);
            String jsonData = json.toString();
            Log.i(tag, "\nData for flutter: " + jsonData);
            result.success(jsonData);
        } catch (Exception e) {
            Map<String, Object> mapData = new HashMap<>();
            mapData.put("message", e.toString());
            mapData.put("success", false);
            result.success(mapData);  
            Log.i(tag, "\nError: " + e);
        }
    }

    private void transferCrypto(String accountId, String privateKey, Result result){
        try {
            //Grab your Hedera Testnet account ID and private key
            AccountId myAccountId = AccountId.fromString(accountId);
            PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

            //Create your Hedera Testnet client
            Client client = Client.forTestnet();
            //Client client = Client.forName("testnet");

            //Set your account as the client's operator
            client.setOperator(myAccountId, myPrivateKey);

            // Set default max transaction fee & max query payment
            client.setMaxTransactionFee(new Hbar(100));
            client.setMaxQueryPayment(new Hbar(50));

            // Generate a new key pair
            PrivateKey newAccountPrivateKey = PrivateKey.generateED25519();
            PublicKey newAccountPublicKey = newAccountPrivateKey.getPublicKey();

            //Create new account and assign the public key
            TransactionResponse newAccount = new AccountCreateTransaction()
                    .setKey(newAccountPublicKey)
                    .setInitialBalance( Hbar.fromTinybars(1000))
                    .execute(client);

            // Get the new account ID
            AccountId newAccountId = newAccount.getReceipt(client).accountId;

            //Transfer HBAR
            TransactionResponse sendHbar = new TransferTransaction()
                    .addHbarTransfer(myAccountId, Hbar.fromTinybars(-1000))
                    .addHbarTransfer(newAccountId, Hbar.fromTinybars(1000))
                    .execute(client);

            String status = sendHbar.getReceipt(client).status.toString();

            //Request the cost of the query
            Hbar queryCost = new AccountBalanceQuery()
                    .setAccountId(newAccountId)
                    .getCost(client);

            //Check the new account's balance
            AccountBalance accountBalanceNew = new AccountBalanceQuery()
                    .setAccountId(newAccountId)
                    .execute(client);

            Map<String, Object> mapData = new HashMap<>();
            mapData.put("success", true);
            mapData.put("id", newAccountId.toString());
            mapData.put("status", status);
            mapData.put("balance", accountBalanceNew.hbars.toString());
            mapData.put("cost", queryCost.toString());
            Log.i(tag, "\nMap: " + mapData);
            JSONObject json = new JSONObject(mapData);
            String jsonData = json.toString();
            Log.i(tag, "\nData for flutter: " + jsonData);
            result.success(jsonData);
        } catch (Exception e) {
            Map<String, Object> mapData = new HashMap<>();
            mapData.put("message", e.toString());
            mapData.put("success", false);
            result.success(mapData);
            Log.i(tag, "\nError: " + e);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
    }
}
