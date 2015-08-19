package milo.server

import scala.collection.immutable.Queue

import akka.actor._
import akka.event._
import akka.io.Tcp
import akka.util.ByteString

import scodec.DecodeResult

import milo.device._

/**
 * Actual tcp connection processor. One connection processor per device connection.
 */
final class DeviceDataProcessor(decoder: DeviceDataDecoder) extends Actor with ActorLogging { 
  import context.{dispatcher, system}

  log.debug(s"${self.path.name} has started processing")

  // Internal actor API
  private case class LoadConfig(deviceId: DeviceId)

  // TODO: definitely request a better buffering scheme...
  private[this] val buffer = Queue.empty[ByteString]

  override def receive: Receive = deviceIdentification

  //////// INTERNAL STATES ////////

  /**
   * Initial state of newly connection processor. By the protocol
   * design the very first incomming message contains device meta
   * information (i.e device UUID, etc... ?)
   */
  def deviceIdentification: Receive = {
    LoggingReceive.withLabel("device identification") {
      case Tcp.Received(data) =>
        decoder.decodeDeviceId(data).fold(handleError, { case DecodeResult(id, rem) =>
          if (rem.nonEmpty) log.warning(s"DeviceID was successfully decoded, but there's an unexpected remainder: $rem")
          context.become(configurationLoading(id))
          self ! LoadConfig(id)
        })        
    }
  }

  /**
   * TODO :: Fail-over strategy (timeouts)
   * TODO :: Proper buffer handling
   */
  def configurationLoading(deviceId: DeviceId): Receive = {
    LoggingReceive.withLabel("loading device configuration") {
      /**
       * Any received device data during the configuration loading,
       * should be properly buffered.
       */
      case Tcp.Received(data) => bufferData(data)

      case LoadConfig(id) => loadConfiguration(id)
      
      case config: DeviceConfiguration =>
        log.info(s"Configuration for device ${deviceId.id} loaded, processing data...")
        context.become(dataProcessor(config))
    }
  }

  /**
   * NOTE :: What is a Device configuration and how are we going to use it?
   */
  private def dataProcessor(config: DeviceConfiguration): Receive = LoggingReceive {
    case Tcp.Received(data) =>
      decoder.decodeDeviceData(data).fold(handleError, { case DecodeResult(data, rem) =>
        log.info(s"Data accepted: $data, rem: $rem")
      })
  }


  private def loadConfiguration(deviceId: DeviceId): Unit = {
    self ! (new DeviceConfiguration {})
  }

  private def bufferData(data: ByteString): Unit = {}

  //// Internal stuff
  private def handleError(reason: String) = {
    log.error(reason)
  }

}
