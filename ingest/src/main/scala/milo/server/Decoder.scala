package milo.server

import akka.util.ByteString
import scodec._, bits._, codecs._, implicits._
import milo.device._

trait DeviceDataDecoder {
  def decodeDeviceId(data: ByteString): Either[String, DecodeResult[DeviceId]]
  def decodeDeviceData(data: ByteString): Either[String, DecodeResult[DeviceData]]
}

object ScodecDecoder extends DeviceDataDecoder {
  def decodeDeviceId(data: ByteString): Either[String, DecodeResult[DeviceId]] = {
    Decoder[DeviceId].decode(BitVector(data.asByteBuffer))
      .fold(err => Left(err.toString), Right(_))
  }

  def decodeDeviceData(data: ByteString): Either[String, DecodeResult[DeviceData]] = {
    Decoder[DeviceData].decode(BitVector(data.asByteBuffer))
      .fold(err => Left(err.toString), Right(_))
  }
}
