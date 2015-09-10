package platform.device

import squants.{Dimensionless, Temperature}

sealed trait Measurement

case class TemperatureMeasurement(value: Temperature) extends Measurement

case class HumidityMeasurement(value: Dimensionless) extends Measurement
