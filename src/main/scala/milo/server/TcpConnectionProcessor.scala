package milo.server

import scala.collection.immutable.Queue

import akka.actor._
import akka.event._
import akka.io.Tcp
import akka.util.ByteString

import scodec._, bits._, codecs._, implicits._

import milo.device._

/**
 * Actual tcp connection processor. One connection processor per device connection.
 */
final class TcpConnectionProcessor(configLoader: DeviceConfigurationLoader) extends Actor with ActorLogging {
  import context.{dispatcher, system}

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
        Decoder[DeviceId].decode(BitVector(data.asByteBuffer)) match {
          case Attempt.Failure(err) => handleError(err.toString)
          case Attempt.Successful(DecodeResult(id, rem)) =>
            if (rem.nonEmpty) log.warning(s"DeviceID was successfully decoded, but there's unexpected remainder: $rem")
            context.become(configurationLoading(id))
            self ! LoadConfig(id)
        }
    }
  }

  /**
   * TODO :: Fail-over strategy (timeouts)
   * TODO :: Proper buffer handling
   */
  def configurationLoading(deviceId: DeviceId): Receive = {
    LoggingReceive.withLabel("loading device configuration") {
      case LoadConfig(id) => loadConfiguration(id)
      case Tcp.Received(data) => bufferData(data)
      case config: DeviceConfiguration =>
        log.info(s"Configuration for device ${deviceId.id} loaded!")
        context.become(dataProcessor(config))
    }
  }

  /**
   * NOTE :: What is a Device configuration and how are we going to use it?
   */
  private def dataProcessor(config: DeviceConfiguration): Receive = LoggingReceive {
    case Tcp.Received(data) =>
      Decoder[DeviceData].decode(BitVector(data.asByteBuffer)) match {
        case Attempt.Failure(err) => handleError(err.toString())
        case Attempt.Successful(deviceData) => // send to kafka
      }
  }


  private def loadConfiguration(deviceId: DeviceId): Unit = {}

  private def bufferData(data: ByteString): Unit = {}

  //// Internal stuff
  private def handleError(reason: String) = {
    log.error(reason)
  }

}
