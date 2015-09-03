package platform.protocols

import akka.util.ByteString
import cats.std.list._
import cats.extensions.std.tryCats._
import cats.Traverse
import common.UUID
import device.units_of_measure.RelativeHumidityUnit.Percent
import device.units_of_measure.ThermalUnit.Kelvin
import milo.protocols.protobuf.device_protocol.DeviceDataPacket.DeviceMeasurement
import milo.protocols.protobuf.device_protocol.DeviceDataPacket.DeviceMeasurement.Measurement.{Humidity, Temperature}
import milo.protocols.protobuf.device_protocol.{DeviceDataPacket, DeviceIdPacket}
import platform.device._
import platform.encoder.DeviceDataEncoder

import scala.util.{Failure, Success, Try}

object ProtoDeviceDataEncoder extends DeviceDataEncoder {

  override def encodeDeviceId(deviceId: DeviceId): Try[ByteString] = {
    Try(DeviceIdPacket(UUID(deviceId.id.getLeastSignificantBits, deviceId.id.getMostSignificantBits))).
      map(_.toByteArray).
      map(ByteString.apply)
  }

  override def encodeDeviceData(deviceData: DeviceData): Try[ByteString] = {
    Traverse[List].sequence(deviceData.measurements.map(toMeasurementPacket).toList).
      map(DeviceDataPacket(deviceData.time, deviceData.seq, _)).
      map(_.toByteArray).
      map(ByteString.apply)
  }

  private[this] def toMeasurementPacket(measurement: Measurement): Try[DeviceMeasurement] = measurement match {
    case TemperatureMeasurement(value)  => Success(DeviceMeasurement(Temperature(DeviceMeasurement.Temperature(value.toKelvinDegrees, Kelvin))))
    case HumidityMeasurement(value)     => Success(DeviceMeasurement(Humidity(DeviceMeasurement.Humidity(value.toPercent, Percent))))
    case _                              => Failure(new MatchError(s"Unrecognized measurement: '$measurement'."))
  }

}
