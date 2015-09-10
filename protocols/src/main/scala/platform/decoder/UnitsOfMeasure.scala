package platform.decoder

import device.units_of_measure.RelativeHumidityUnit.Percent
import device.units_of_measure.{RelativeHumidityUnit, ThermalUnit}
import device.units_of_measure.ThermalUnit.{Fahrenheit, Celsius, Kelvin}
import milo.protocols.protobuf.device_protocol.DeviceDataPacket.{DeviceMeasurement => p}
import milo.protocols.protobuf.device_protocol.DeviceDataPacket.DeviceMeasurement.{Measurement => d}
import squants.DimensionlessUnit
import squants.thermal.TemperatureScale
import platform.device.{Measurement, TemperatureMeasurement, HumidityMeasurement}

import scala.util.{Failure, Success, Try}

object UnitsOfMeasure {

  def toSquant(x: p.Measurement): Try[Measurement] = x match {
    case d.Temperature(temperature) => toSquantTemperature(temperature)
    case d.Humidity(humidity)       => toSquantHumidity(humidity)
    case _                          => Failure(new MatchError(s"Unrecognized measurement: '$x'."))
  }

  def toSquantTemperature(x: p.Temperature): Try[Measurement] = {
    toSquantTemperatureUnit(x.unit).
      map(_.apply(x.value)).
      map(TemperatureMeasurement)
  }

  def toSquantTemperatureUnit(x: ThermalUnit): Try[TemperatureScale] = x match {
    case Kelvin     => Success(squants.thermal.Kelvin)
    case Celsius    => Success(squants.thermal.Celsius)
    case Fahrenheit => Success(squants.thermal.Fahrenheit)
    case _          => Failure(new MatchError(s"Unrecognized thermal unit: '$x'."))
  }

  def toSquantHumidity(x: p.Humidity): Try[Measurement] = {
    toSquantHumidityUnit(x.unit).
      map(_.apply(x.value)).
      map(HumidityMeasurement)
  }

  def toSquantHumidityUnit(x: RelativeHumidityUnit): Try[DimensionlessUnit] = x match {
    case Percent  => Success(squants.Percent)
    case _        => Failure(new MatchError(s"Unrecognized humidity unit: '$x'."))
  }

}
