package milo.decoder

import java.util.UUID
import scala.util.{Try, Success, Failure}
import akka.util.ByteString

import scodec.bits.ByteVector
import scodec.Decoder

import milo.device.{DeviceId, DeviceData}

object Decoders {

  implicit val deviceIdDecoder: Decoder.Aux[({ type λ[α] = Either[(Throwable, String), α]})#λ, DeviceId] =
    new Decoder[DeviceId] {
      type F[x] = Either[(Throwable, String), x]
      def decoder(data: ByteString): Either[(Throwable, String), DeviceId] = {
        Try(UUID.nameUUIDFromBytes(data.toArray)) match {
          case Failure(ex) => Left((ex, s"DeviceId decoding failed, reason: ${ex.getMessage}"))
          case Success(id) => Right(DeviceId(id))
        }
      }
    }

  implicit val deviceDataDecoder = {
    new Decoder[DeviceData] {
      import scodec.codecs._

      def decoder(data: ByteString) = {

      }

    }
  }



}
