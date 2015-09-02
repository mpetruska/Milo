package platform.protocols

import akka.util.ByteString
import common.UUID
import milo.protocols.protobuf.device_protocol.{DeviceDataPacket, DeviceIdPacket}
import platform.device.{DeviceId, DeviceData}
import platform.encoder.DeviceDataEncoder

import scala.util.Try

object ProtoDeviceDataEncoder extends DeviceDataEncoder {

  override def encodeDeviceId(deviceId: DeviceId): Try[ByteString] = {
    Try(DeviceIdPacket(UUID(deviceId.id.getLeastSignificantBits, deviceId.id.getMostSignificantBits))).
      map(_.toByteArray).
      map(ByteString.apply)
  }

  override def encodeDeviceData(deviceData: DeviceData): Try[ByteString] = {
    ???
  }

}
