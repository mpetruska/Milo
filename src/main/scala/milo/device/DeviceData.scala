package milo.device

import scodec._, bits._, codecs._

final case class DeviceDataFrame(
  `type`: Byte,
  unit: Byte,
  data: ByteVector)

object DeviceDataFrame {
  implicit val frameCodec: Codec[DeviceDataFrame] = (
    ("t"     | byte) ::
    ("u"     | byte) ::
    ("value" | bytes(8))
  ).as[DeviceDataFrame]

  val ddfList: Codec[List[DeviceDataFrame]] = list(frameCodec)
}

final case class DeviceData(
  time: Long,
  seq: Int,
  length: Short,
  data: List[DeviceDataFrame])

object DeviceData {
  implicit val dataCodec: Codec[DeviceData] = (
    ("time" | int64) ::
    ("seq"  | int32) ::
    ("cnt"  | short16) ::
    ("data" | DeviceDataFrame.ddfList)
  ).as[DeviceData]
}