package milo.device

import scodec._, bits._, codecs._

final case class DeviceData(seq: Int, data: ByteVector)

object DeviceData {
  implicit val dataCodec = (
    ("seq"  | int32) ::
    ("data" | bytes(8))
  ).as[DeviceData]
}
