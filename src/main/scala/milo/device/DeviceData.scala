package milo.device

import scodec.bits.ByteVector

final case class DeviceDataFrame(
  `type`: Byte,
  unit: Byte,
  data: ByteVector)

final case class DeviceData(
  time: Long,
  seq: Int,
  length: Short,
  data: List[DeviceDataFrame])
