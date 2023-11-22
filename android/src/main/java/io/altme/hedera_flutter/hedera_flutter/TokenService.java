package io.altme.hedera_flutter.hedera_flutter;

import android.util.Log;

import com.google.errorprone.annotations.Var;
import com.hedera.hashgraph.sdk.*;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.flutter.plugin.common.MethodChannel;

public class TokenService {
    private static final String tag = "hedera";

    static void transferToken(String accountId, String privateKey, String network, MethodChannel.Result result) {
        try {
            //Grab your Hedera Testnet account ID and private key
            AccountId myAccountId = AccountId.fromString(accountId);
            PrivateKey myPrivateKey = PrivateKey.fromString(privateKey);

            //Create your Hedera Testnet client
            Client client = Client.forName(network);

            // Defaults the operator account ID and key such that all generated transactions will be paid for
            // by this account and be signed by this key
            client.setOperator(myAccountId, myPrivateKey);


            // Generate a Ed25519 private, public key pair
            PrivateKey key1 = PrivateKey.generateED25519();
            PrivateKey key2 = PrivateKey.generateED25519();

            Log.i(tag, "private key = " + key1);
            Log.i(tag, "public key = " + key1.getPublicKey());
            Log.i(tag, "private key = " + key2);
            Log.i(tag, "public key = " + key2.getPublicKey());

            @Var TransactionResponse response = new AccountCreateTransaction()
                    // The only _required_ property here is `key`
                    .setKey(key1.getPublicKey())
                    .setInitialBalance(Hbar.fromTinybars(1000))
                    .execute(client);

            // This will wait for the receipt to become available
            @Var TransactionReceipt receipt = response.getReceipt(client);

            AccountId accountId1 = Objects.requireNonNull(receipt.accountId);

            Log.i(tag, "accountId1 = " + accountId1);

            response = new AccountCreateTransaction()
                    // The only _required_ property here is `key`
                    .setKey(key2.getPublicKey())
                    .setInitialBalance(Hbar.fromTinybars(1000))
                    .execute(client);

            // This will wait for the receipt to become available
            receipt = response.getReceipt(client);

            AccountId accountId2 = Objects.requireNonNull(receipt.accountId);

            Log.i(tag, "accountId2 = " + accountId1);

            response = new TokenCreateTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .setTokenName("ffff")
                    .setTokenSymbol("F")
                    .setDecimals(3)
                    .setInitialSupply(1000000)
                    .setTreasuryAccountId(myAccountId)
                    .setAdminKey(myPrivateKey.getPublicKey())
                    .setFreezeKey(myPrivateKey.getPublicKey())
                    .setWipeKey(myPrivateKey.getPublicKey())
                    .setKycKey(myPrivateKey.getPublicKey())
                    .setSupplyKey(myPrivateKey.getPublicKey())
                    .setFreezeDefault(false)
                    .execute(client);

            TokenId tokenId = Objects.requireNonNull(response.getReceipt(client).tokenId);
            Log.i(tag, "token = " + tokenId);


            new TokenAssociateTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .setAccountId(accountId1)
                    .setTokenIds(Collections.singletonList(tokenId))
                    .freezeWith(client)
                    .sign(myPrivateKey)
                    .sign(key1)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Associated account " + accountId1 + " with token " + tokenId);

            new TokenAssociateTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .setAccountId(accountId2)
                    .setTokenIds(Collections.singletonList(tokenId))
                    .freezeWith(client)
                    .sign(myPrivateKey)
                    .sign(key2)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Associated account " + accountId2 + " with token " + tokenId);

            new TokenGrantKycTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .setAccountId(accountId1)
                    .setTokenId(tokenId)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Granted KYC for account " + accountId1 + " on token " + tokenId);

            new TokenGrantKycTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .setAccountId(accountId2)
                    .setTokenId(tokenId)
                    .execute(client)
                    .getReceipt(client);

            System.out.println("Granted KYC for account " + accountId2 + " on token " + tokenId);

            new TransferTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .addTokenTransfer(tokenId, myAccountId, -10)
                    .addTokenTransfer(tokenId, accountId1, 10)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Sent 10 tokens from account " + myAccountId + " to account " + accountId1 + " on token " + tokenId);

            new TransferTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .addTokenTransfer(tokenId, accountId1, -10)
                    .addTokenTransfer(tokenId, accountId2, 10)
                    .freezeWith(client)
                    .sign(key1)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Sent 10 tokens from account " + accountId1 + " to account " + accountId2 + " on token " + tokenId);

            new TransferTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .addTokenTransfer(tokenId, accountId2, -10)
                    .addTokenTransfer(tokenId, accountId1, 10)
                    .freezeWith(client)
                    .sign(key2)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Sent 10 tokens from account " + accountId2 + " to account " + accountId1 + " on token " + tokenId);

            new TokenWipeTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .setTokenId(tokenId)
                    .setAccountId(accountId1)
                    .setAmount(10)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Wiped balance of account " + accountId1);

            new TokenDeleteTransaction()
                    .setNodeAccountIds(Collections.singletonList(response.nodeId))
                    .setTokenId(tokenId)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Deleted token " + tokenId);

            new AccountDeleteTransaction()
                    .setAccountId(accountId1)
                    .setTransferAccountId(myAccountId)
                    .freezeWith(client)
                    .sign(myPrivateKey)
                    .sign(key1)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Deleted accountId1 " + accountId1);

            new AccountDeleteTransaction()
                    .setAccountId(accountId2)
                    .setTransferAccountId(myAccountId)
                    .freezeWith(client)
                    .sign(myPrivateKey)
                    .sign(key2)
                    .execute(client)
                    .getReceipt(client);

            Log.i(tag, "Deleted accountId2" + accountId2);


            Map<String, Object> mapData = new HashMap<>();
            mapData.put("success", true);
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
            Log.e(tag, "\nError: " + e);
        }
    }
}