import Flutter
import UIKit
import SwiftUI
import Combine

class HederaChannelHandler: NSObject {
    
    static let shared = HederaChannelHandler()
    
    private var cancelBag = Set<AnyCancellable>()
    
    func createAccount(call: FlutterMethodCall, result: @escaping FlutterResult) {
        let args: NSDictionary = call.arguments as! NSDictionary
        let accountId: String = args["accountId"] as! String
        let privateKey: String = args["privateKey"] as! String

        HederaChannelService.shared.createAccount(accountId: accountId, privateKey:privateKey)
            .sink(receiveCompletion: {  (completion) in
                result([
                    "success": false,
                    "message": "Failed to create Account"
                ])
            }, receiveValue: { data in
                result([
                    "success": true,
                    "map": data
                ])
            })
            .store(in: &cancelBag)
    }
 
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        cancelBag.removeAll()
        return nil
    }
    
} 
