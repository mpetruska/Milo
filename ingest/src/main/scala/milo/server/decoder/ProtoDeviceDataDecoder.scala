package milo.server.decoder

import java.util.UUID

import akka.util.ByteString
import milo.device.{Measurement, DeviceData, DeviceId}
import milo.protocol.device_protocol.{DeviceDataPacket, DeviceIdPacket}

import scala.util.Try

/**
 * Helper to convert google protobuf generated classes into domain specific classes
 */
object ProtoUnwrappers {
  case class ProtoDeviceData(time: Long, seq: Int, measurements: Seq[Measurement] = Nil) extends DeviceData

  object ProtoDeviceData{
    def tryFrom(ddp: DeviceDataPacket) = Try{
      val measurements = ddp.measurements.map(ProtoMeasurement.from)
      ProtoDeviceData(ddp.time, ddp.seq, measurements)
    }
  }

  case class ProtoMoistureMeasurement(value: Double) extends Measurement

  case class ProtoTemperatureMeasurement(value: Double) extends Measurement

  object ProtoMeasurement{
    def from(ddp: DeviceDataPacket.DeviceMeasurement) = ddp.measurement match {
      case DeviceDataPacket.DeviceMeasurement.Measurement.Moisture(moisture) =>
        ProtoMoistureMeasurement(moisture.value)
      case DeviceDataPacket.DeviceMeasurement.Measurement.Temperature(temperature) =>
        ProtoTemperatureMeasurement(temperature.value)
      case DeviceDataPacket.DeviceMeasurement.Measurement.Empty =>
        throw new IllegalArgumentException("DeviceDataPacket.DeviceMeasurement.Measurement is 'Empty'.")
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

