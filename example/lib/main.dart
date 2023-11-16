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
  String id = '';
  String balance = '';
  String status = '';
  String cost = '';
  String error = '';

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

      if (value['success']) {
        setState(() {
          id = value['id'];
          balance = value['balance'];
        });
      } else {
        setState(() {
          error = value.toString();
        });
      }
    } catch (e) {
      setState(() {
        error = e.toString();
      });
    }
  }

  Future<void> transferCrypto() async {
    try {
      await dotenv.load();
      final accountId = dotenv.get('MY_ACCOUNT_ID');
      final privateKey = dotenv.get('MY_PRIVATE_KEY');
      final value = await _hederaFlutterPlugin.transferCrypto(
        accountId: accountId,
        privateKey: privateKey,
      );

      if (value['success']) {
        setState(() {
          id = value['id'];
          status = value['status'];
          balance = value['balance'];
          cost = value['cost'];
        });
      } else {
        setState(() {
          error = value.toString();
        });
      }
    } catch (e) {
      setState(() {
        error = e.toString();
      });
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
              Text('Account Id: $id\n'),
              Text('Balance: $balance\n'),
              const SizedBox(height: 10),
              OutlinedButton(
                onPressed: () => createAccount(),
                child: const Text("Create Account"),
              ),
              const SizedBox(height: 10),
              Text('Cost: $cost\n'),
              Text('Status: $status\n'),
              const SizedBox(height: 10),
              OutlinedButton(
                onPressed: () => transferCrypto(),
                child: const Text("Transfer Crypto"),
              ),
              const SizedBox(height: 10),
              Text('Error: $error\n'),
            ],
          ),
        ),
      ),
    );
  }
}
