package milo.device

import java.util.UUID

import scodec._, bits._, codecs._

final case class DeviceId(id: UUID) extends AnyVal

object DeviceId {
  implicit val deviceIdCodec = uuid.map(DeviceId(_))
}
