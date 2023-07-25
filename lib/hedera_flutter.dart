
import 'hedera_flutter_platform_interface.dart';

class HederaFlutter {
  Future<String?> getPlatformVersion() {
    return HederaFlutterPlatform.instance.getPlatformVersion();
  }
}
