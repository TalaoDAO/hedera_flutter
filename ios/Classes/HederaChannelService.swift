import Combine
import Hedera

typealias Completion<T> = (Result<T, Error>) -> Void

class HederaChannelService{
    
    static var shared = HederaChannelService()

    func createAccount(
    accountId: String,
    privateKey: String ) -> AnyPublisher<String, Error> {
        return  Future<String, Error> { [self] (promise) in
            let env = try Dotenv.load()
            let client = try Client.forName("testnet")

            client.setOperator(accountId, privateKey)

            let newKey = PrivateKey.generateEd25519()

            print("private key = \(newKey)")
            print("public key = \(newKey.publicKey)")

            let response = try await AccountCreateTransaction()
                .key(.single(newKey.publicKey))
                .initialBalance(5)
                .execute(client)

            let receipt = try await response.getReceipt(client)
            let newAccountId = receipt.accountId!

            print("account address = \(newAccountId)")
            promise(.success(newAccountId))
        }
        .eraseToAnyPublisher()
    }
     
}
 
