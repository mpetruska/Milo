package milo.server

import akka.actor._
import akka.event._
import akka.io.Tcp
import akka.pattern.pipe
import akka.util.ByteString
import milo.device._

import scala.collection.immutable.Queue
import scala.concurrent.Future

final class TcpConnectionProcessor(configLoader: DeviceConfigurationLoader) extends Actor {
  import context.{dispatcher, system}

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
