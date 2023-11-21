package io.altme.hedera_flutter.hedera_flutter;

import android.util.Log;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.AccountInfo;
import com.hedera.hashgraph.sdk.AccountInfoQuery;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.EvmAddress;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Mnemonic;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionReceiptQuery;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

public class BasicService {
    private static final String tag = "hedera";

    static void queryBalance(String accountId, String network, MethodChannel.Result result) {
        try {
            //Create your Hedera Testnet client
            Client client = Client.forName(network);

            AccountId oldAccountId = AccountId.fromString(accountId);

            //Check the new account's balance
            AccountBalance accountBalance = new AccountBalanceQuery()
                    .setAccountId(oldAccountId)
                    .execute(client);

            Log.i(tag, "\nAccount balance: " + accountBalance);

            Map<String, Object> mapData = new HashMap<>();
            mapData.put("success", true);
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
}