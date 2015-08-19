package milo.server.decoder

import akka.util.ByteString
import milo.device._

import scala.util.Try

/**
 * Convert binary representations of device data to object classes. The encoding / decoding used is decoder specific.
 */
trait DeviceDataDecoder {
  /**
   * Decode binary data to a device id
   * @param data encoded data
   * @return decoded device id
   */
  def decodeDeviceId(data: ByteString): Try[DeviceId]

  /**
   * Decode binary data to device data (containing measurements)
   * @param data encoded data
   * @return decoded device data
   */
  def decodeDeviceData(data: ByteString): Try[DeviceData]
}
