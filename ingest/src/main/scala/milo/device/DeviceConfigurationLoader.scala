package milo.device

import scala.concurrent.Future

abstract class DeviceConfigurationLoader {
  def load(deviceId: DeviceId): Future[DeviceConfiguration]
}