package milo

import scala.collection.immutable.Queue
import scala.concurrent.Future

import akka.actor._
import akka.io.Tcp
import akka.event._
import akka.util.ByteString
import akka.pattern.pipe

import milo.decoder._, Decoders._
import milo.device._

trait DeviceConfiguration 

abstract class DeviceConfigurationLoader {
  def load(deviceId: DeviceId): Future[DeviceConfiguration]
}

final class TcpConnectionProcessor(configLoader: DeviceConfigurationLoader) extends Actor {
  import context.system
  import context.dispatcher

  // Just a shorthand
  private[this] val log = system.log

  // Internal actor commands
  private case class LoadConfig(deviceId: DeviceId)

  private[this] val buffer = Queue.empty[ByteString]

  override def receive: Receive = LoggingReceive {
    case Tcp.Received(data) =>
      Decoder[DeviceId].decode(data) match {
        case Left((error, reason)) => log.error(error, reason)
        case Right(deviceId @ DeviceId(uuid)) =>
          log.info(s"New device connected: $uuid, loading device configuration...")
          context.become(loadingConfig)
          self ! LoadConfig(deviceId)
      }
  }

  /**
   * To think:
   *   1. Fail-over strategy (timeouts)
   *   2. Proper buffer handling
   */
  private def loadingConfig: Receive = LoggingReceive {
    case Tcp.Received(data) => buffer :+ data
    case LoadConfig(deviceId) => configLoader.load(deviceId) pipeTo self
    case config: DeviceConfiguration => context.become(dataProcessor(config))
  }
  
  /**
   * To think:
   *   1. What is a Device configuration and how are we going to use it?
   */
  private def dataProcessor(config: DeviceConfiguration): Receive = LoggingReceive {
    case Tcp.Received(data) =>
      
  }

}
