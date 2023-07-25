package io.altme.hedera_flutter.hedera_flutter;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import android.util.Log;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountBalance;


import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** HederaFlutterPlugin */
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
        createAccount(accountId, privateKey, result);
        break;
      default:
        result.notImplemented();
    }

  }

  private void createAccount(String accountId, String privateKey, Result result) {
    try {
        //Grab your Hedera Testnet account ID and private key
        AccountId myAccountId = AccountId.fromString(accountId);
        PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

        //Create your Hedera Testnet client
        Client client = Client.forTestnet();
        client.setOperator(myAccountId, myPrivateKey);

        // Set default max transaction fee & max query payment
        client.setDefaultMaxTransactionFee(new Hbar(100));
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

        Log.i(tag, "\nNew account ID: " +newAccountId);

        //Check the new account's balance
        if(newAccountId == null){
          Map<String, Object> mapData = new HashMap<>();
          mapData.put("success", false);
          result.success(mapData);
          return;
        };

        AccountBalance accountBalance = new AccountBalanceQuery()
                .setAccountId(newAccountId)
                .execute(client);

        Log.i(tag, "The new account balance: " +accountBalance.hbars);


        Map<String, Object> mapData = new HashMap<>();
        mapData.put("message", true);
        mapData.put("id", newAccountId);
        mapData.put("balance", accountBalance.hbars);
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
