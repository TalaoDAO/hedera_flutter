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
  String accountId = '';
  String balance = '';

  final _hederaFlutterPlugin = HederaFlutter();

  @override
  void initState() {
    super.initState();
  }

  Future<void> createAccount() async {
    try {
      await dotenv.load();
      final accountId = dotenv.get('MY_ACCOUNT_ID');
      final privateKey = dotenv.get('MY_PRIVATE_KEY');
      final value = await _hederaFlutterPlugin.createAccount(
        accountId: accountId,
        privateKey: privateKey,
      );
      print(value);
    } catch (e) {
      print(e);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Hedera example app'),
        ),
        body: Center(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const SizedBox(height: 30),
              Text('Account Id: $accountId\n'),
              Text('Balance: $balance\n'),
              const SizedBox(height: 10),
              OutlinedButton(
                onPressed: () => createAccount(),
                child: const Text("Create Account"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
