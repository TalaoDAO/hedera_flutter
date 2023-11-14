import Flutter
import UIKit

public class HederaFlutterPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "hederaMethod", binaryMessenger: registrar.messenger())
    let instance = HederaFlutterPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "createAccount":
        HederaChannelHandler.shared.createAccount(call: call, result: result)
    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
