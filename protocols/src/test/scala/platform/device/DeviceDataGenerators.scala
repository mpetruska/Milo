package platform.device

import squants._
import org.scalacheck.{Gen, Arbitrary}

trait DeviceDataGenerators {

  lazy val deviceIdGen = {
    Gen.uuid map DeviceId
  }

  lazy val temperatureMeasurementGen = {
    Gen.chooseNum[Double](0, 10000.0) map Kelvin.apply[Double] map TemperatureMeasurement
  }

  lazy val humidityMeasurementGen = {
    Gen.chooseNum[Double](0.0, 100.0) map Percent.apply[Double] map HumidityMeasurement
  }

  lazy val measurementGen = {
    Gen.oneOf(temperatureMeasurementGen, humidityMeasurementGen)
  }

  lazy val deviceDataGen = {
    for {
      time <- Gen.chooseNum[Long](0L, Long.MaxValue)
      seq <- Gen.chooseNum[Int](0, Int.MaxValue)
      measurements <- Gen.listOf(measurementGen)
    } yield DeviceData(time, seq, measurements)
  }

  implicit lazy val arbitraryDeviceId = Arbitrary(deviceIdGen)

  implicit lazy val arbitraryDeviceData = Arbitrary(deviceDataGen)

}
