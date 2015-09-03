package platform.protocols

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import platform.device.{DeviceData, DeviceDataGenerators, DeviceId}

import scala.util.{Success, Try}

class ProtoBufSpecification extends Properties("ProtoBuf protocol")
  with DeviceDataGenerators {

  import ProtoDeviceDataEncoder._
  import ProtoDeviceDataDecoder._

  property("roundtrip DeviceId") = forAll { (x: DeviceId) =>
    roundTripDeviceId(x) == Success(x)
  }

  property("roundtrip DeviceData") = forAll { (x: DeviceData) =>
    roundTripDeviceData(x) == Success(x)
  }

  def roundTripDeviceId(x: DeviceId): Try[DeviceId] = {
    for {
      encoded <- encodeDeviceId(x)
      decoded <- decodeDeviceId(encoded)
    } yield decoded
  }

  def roundTripDeviceData(x: DeviceData): Try[DeviceData] = {
    for {
      encoded <- encodeDeviceData(x)
      decoded <- decodeDeviceData(encoded)
    } yield decoded
  }

}
