package platform.encoder

import akka.util.ByteString
import platform.device.{DeviceData, DeviceId}

import scala.util.Try

trait DeviceDataEncoder {

  def encodeDeviceId(deviceId: DeviceId): Try[ByteString]

  def encodeDeviceData(deviceData: DeviceData): Try[ByteString]

}
