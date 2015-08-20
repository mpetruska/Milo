package milo.server

import akka.stream.actor.ActorPublisher
import kafka.producer.KafkaProducer
import kafka.serializer.StringEncoder
import milo.infrastructure.ReactiveKafkaConnection
import milo.server.decoder.DeviceDataDecoder

import scala.collection.immutable.Queue
import scala.util.{Success, Failure}

import akka.actor._
import akka.event._
import akka.io.Tcp
import akka.util.ByteString
import milo.device._

object AbstractDeviceDataProcessor{
  // Internal actor API
  private case class LoadConfig(deviceId: DeviceId)
}

/**
 * Actual tcp connection processor. One connection processor per device connection.
 */
abstract class AbstractDeviceDataProcessor extends Actor with ActorLogging {
  import AbstractDeviceDataProcessor._

  def decoder: DeviceDataDecoder

  def publish(deviceData: DeviceData): Unit

  log.debug(s"${self.path.name} has started processing")

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
        decoder.decodeDeviceId(data) match {
          case Failure(er) => handleError(er.toString)
          case Success(id) =>
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
      decoder.decodeDeviceData(data) match {
        case Failure(er) => handleError(er.toString)
        case Success(data) =>
          log.info(s"Data accepted: $data")
          publish(data)
      }
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

/**
 * Implementation of a data processor that uses kafka to publish device data
 * @param deviceDataDecoder
 */
class KafkaDeviceDataProcessor(val deviceDataDecoder: DeviceDataDecoder)
  extends AbstractDeviceDataProcessor
  with ActorPublisher[String]
  with ReactiveKafkaConnection{

  implicit val kafkaEncoder = new StringEncoder()

  // TODO :: Extract topic to config file
  lazy val producer: KafkaProducer[String] = producerFor(topic = "processed")

  def decoder = deviceDataDecoder

  def publish(value: DeviceData): Unit = {
    producer.send(value.toString) // TODO :: Convert message to Kafka format
  }
}
