package io.altme.hedera_flutter.hedera_flutter;

import android.util.Log;

import com.hedera.hashgraph.sdk.*;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

public class AccountService {
    private static final String tag = "hedera";

    static void createAccount(String accountId, String privateKey, String network, MethodChannel.Result result) {
        try {
            //Grab your Hedera Testnet account ID and private key
            AccountId myAccountId = AccountId.fromString(accountId);
            PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

            //Create your Hedera Testnet client
            Client client = Client.forName(network);

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
            mapData.put("accountId", newAccountId.toString());
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

    static void createAccountWithAlias(String accountId, String privateKey, String network, MethodChannel.Result result) {
        try {
            //Grab your Hedera Testnet account ID and private key
            AccountId myAccountId = AccountId.fromString(accountId);
            PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

            //Create your Hedera Testnet client
            Client client = Client.forName(network);

            // Defaults the operator account ID and key such that all generated transactions will be paid for
            // by this account and be signed by this key
            client.setOperator(myAccountId, myPrivateKey);

            PrivateKey privateKey1 = PrivateKey.generateECDSA();
            Log.i(tag, "\nprivateKey1: " + privateKey1);

            PublicKey publicKey = privateKey1.getPublicKey();

            EvmAddress evmAddress = publicKey.toEvmAddress();
            Log.i(tag, "\nevmAddress: " + evmAddress);

            AccountCreateTransaction accountCreateTransaction = new AccountCreateTransaction()
                    .setInitialBalance(Hbar.fromTinybars(100))
                    .setKey(myPrivateKey)
                    .setAlias(evmAddress)
                    .freezeWith(client);

            accountCreateTransaction.sign(privateKey1);
            TransactionResponse response = accountCreateTransaction.execute(client);

            AccountId newAccountId = new TransactionReceiptQuery()
                    .setTransactionId(response.transactionId)
                    .execute(client)
                    .accountId;

            Log.i(tag, "\nNew account ID: " + newAccountId);

            AccountInfo accountInfo = new AccountInfoQuery()
                    .setAccountId(newAccountId)
                    .execute(client);

            if (accountInfo.contractAccountId != null) {
                Log.i(tag, "\nThe new account has alias : " + accountInfo.contractAccountId);
            } else {
                throw new Exception("The new account doesn't have alias");
            }

            Map<String, Object> mapData = new HashMap<>();
            mapData.put("success", true);
            mapData.put("accountId", newAccountId.toString());
            mapData.put("alias", accountInfo.contractAccountId.toString());
            mapData.put("privateKey", privateKey1.toString());
            mapData.put("evmAddress", evmAddress.toString());
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

    static void createAccountWithMnemonics(String accountId, String privateKey, String network, String mnemonicsString, MethodChannel.Result result) {
        try {
            //Grab your Hedera Testnet account ID and private key
            AccountId myAccountId = AccountId.fromString(accountId);
            PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

            //Create your Hedera Testnet client
            Client client = Client.forName(network);

            // Defaults the operator account ID and key such that all generated transactions will be paid for
            // by this account and be signed by this key
            client.setOperator(myAccountId, myPrivateKey);

            Mnemonic mnemomic = Mnemonic.fromString(mnemonicsString);
            PrivateKey newAccountPrivateKey = mnemomic.toStandardEd25519PrivateKey("", 0);

            PublicKey newAccountPublicKey = newAccountPrivateKey.getPublicKey();

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
            mapData.put("accountId", newAccountId.toString());
            mapData.put("balance", accountBalance.hbars.toString());
            mapData.put("mnemonics", mnemonicsString);
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

    static void transferHbar(String accountId, String privateKey, String network, MethodChannel.Result result){
        try {
            //Grab your Hedera Testnet account ID and private key
            AccountId myAccountId = AccountId.fromString(accountId);
            PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

            //Create your Hedera Testnet client
            Client client = Client.forName(network);

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
            mapData.put("accountId", newAccountId.toString());
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
}