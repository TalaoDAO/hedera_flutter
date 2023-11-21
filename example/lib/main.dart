import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:hedera_flutter/hedera_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String accId = '';
  String value = '';

  final _hederaFlutterPlugin = HederaFlutter();

  @override
  void initState() {
    getEnvData();
    super.initState();
  }

  String accountId = '';
  String privateKey = '';

  getEnvData() async {
    await dotenv.load();
    accountId = dotenv.get('MY_ACCOUNT_ID');
    privateKey = dotenv.get('MY_PRIVATE_KEY');
  }

  // testnet mainnet previewnet
  final network = 'testnet';
  final mnemonicsString =
      "engage element country comfort chase oxygen biology rescue network produce seat imitate ketchup still security analyst shoulder board album require shuffle argue decorate language";

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Hedera Example app'),
        ),
        body: Center(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const SizedBox(height: 30),
              Text('Network: $network'),
              const SizedBox(height: 5),
              Text('Account Id: $accId'),
              const SizedBox(height: 30),

              /// create Account
              OutlinedButton(
                onPressed: () async {
                  try {
                    setState(() => value = 'Loading...');
                    final data = await _hederaFlutterPlugin.createAccount(
                        accountId: accountId,
                        privateKey: privateKey,
                        network: network);
                    setState(() {
                      accId = data['accountId'];
                      value = data.toString();
                    });
                  } catch (e) {
                    setState(() => value = e.toString());
                  }
                },
                child: const Text("Create Account"),
              ),

              /// create Account With Alias
              OutlinedButton(
                onPressed: () async {
                  try {
                    setState(() => value = 'Loading...');
                    final data =
                        await _hederaFlutterPlugin.createAccountWithAlias(
                            accountId: accountId,
                            privateKey: privateKey,
                            network: network);
                    setState(() {
                      accId = data['accountId'];
                      value = data.toString();
                    });
                  } catch (e) {
                    setState(() => value = e.toString());
                  }
                },
                child: const Text("Create Account With Alias"),
              ),

              /// create Account With Mnemonics
              OutlinedButton(
                onPressed: () async {
                  try {
                    setState(() => value = 'Loading...');

                    final data =
                        await _hederaFlutterPlugin.createAccountWithMnemonics(
                      accountId: accountId,
                      privateKey: privateKey,
                      network: network,
                      mnemonicsString: mnemonicsString,
                    );
                    setState(() {
                      accId = data['accountId'];
                      value = data.toString();
                    });
                  } catch (e) {
                    setState(() => value = e.toString());
                  }
                },
                child: const Text("Create Account With Mnemonics"),
              ),

              /// transfer Crypto
              OutlinedButton(
                onPressed: () async {
                  try {
                    setState(() => value = 'Loading...');
                    final data = await _hederaFlutterPlugin.queryBalance(
                        accountId: accId, network: network);
                    setState(() => value = data.toString());
                  } catch (e) {
                    setState(() => value = e.toString());
                  }
                },
                child: const Text("Query Balance"),
              ),

              /// transfer Crypto
              OutlinedButton(
                onPressed: () async {
                  try {
                    setState(() => value = 'Loading...');
                    final data = await _hederaFlutterPlugin.transferHbar(
                        accountId: accountId,
                        privateKey: privateKey,
                        network: network);
                    setState(() => value = data.toString());
                  } catch (e) {
                    setState(() => value = e.toString());
                  }
                },
                child: const Text("Transfer Hbar"),
              ),
              const SizedBox(height: 5),
              const Divider(),
              const SizedBox(height: 5),
              SizedBox(
                width: double.infinity,
                child: SelectableText(
                  value,
                  textAlign: TextAlign.left,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
