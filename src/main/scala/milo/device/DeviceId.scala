package milo.device

import java.util.UUID

import scodec.bits.ByteVector
import scodec.{Decoder, Codec}

final case class DeviceId(id: UUID) extends AnyVal
