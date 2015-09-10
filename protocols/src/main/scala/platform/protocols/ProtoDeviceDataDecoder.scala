package platform.protocols

import java.util.UUID

import akka.util.ByteString
import cats.Traverse
import cats.std.list._
import cats.extensions.std.tryCats._
import milo.protocols.protobuf.device_protocol.{DeviceDataPacket, DeviceIdPacket}
import platform.decoder.DeviceDataDecoder
import platform.decoder.UnitsOfMeasure._
import platform.device._

import scala.util.Try

/**
 * Helper to convert google protobuf generated classes into domain specific classes
 */
object ProtoUnwrappers {

  object ProtoDeviceData {
    def tryFrom(ddp: DeviceDataPacket) = {
      val measurements = ddp.measurements.map(_.measurement).map(toSquant).toList
      Traverse[List].sequence(measurements).map(DeviceData(ddp.time, ddp.seq, _))
    }
  }

}

/**
 * Decoder implementation based on google protocol buffers version 2.
 */
object ProtoDeviceDataDecoder extends DeviceDataDecoder {
  import ProtoUnwrappers._

  /**
   * Decode device id using google protobuf protocol
   * @param data protobuf encoded data
   * @return decoded device id
   */
  def decodeDeviceId(data: ByteString): Try[DeviceId] = {
    DeviceIdPacket.validate(data.toArray).map{ deviceIdPacket =>
      val uuid = new UUID(deviceIdPacket.id.mostSignificantBits, deviceIdPacket.id.leastSignificantBits)
      DeviceId(uuid)
    }
  }

  /**
   * Decode device measurements using google protobuf protocol
   * @param data protobuf encoded data
   * @return decoded device data
   */
  def decodeDeviceData(data: ByteString): Try[DeviceData] = {
    DeviceDataPacket.validate(data.toArray).flatMap(ProtoDeviceData.tryFrom)
  }
}

