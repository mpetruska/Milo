package milo.server

import java.net.InetSocketAddress

import akka.actor._
import akka.event.LoggingReceive
import akka.io.{IO, Tcp}
import milo.server.decoder.ProtoDeviceDataDecoder

import scala.concurrent.duration.Duration

/**
 * Extremely basic implementation of Tcp server. For the time being, the only
 * actor's logic is to register (bind) itself on the tcp manager level and
 * start accepting tcp connections. Per each device connection, the server
 * creates a data processor, which does the actuall processing.
 * TODO: fine grained tcp connection control, with back-pressure and fail-over strategy
 */
final class MiloTcpServer(socket: InetSocketAddress) extends Actor with ActorLogging {
  import context.system

  private val connectionCounter = Iterator.from(0)

  /**
   * Register self as a server, listening for incomming Tcp connections.
   */
  log.info(s"Registering server on socket: $socket")
  IO(Tcp) ! Tcp.Bind(self, socket)

  override def receive: Receive = LoggingReceive {
    /**
     * When the actor is registered by the Tcp Manager, it accepts Tcp.Bound message,
     * signalling that this actor is ready to accept incomming tcp connections
     */
    case Tcp.Bound(localAddr) =>
      log.info(s"MiloTcpServer is ready to listen for incomming connections on $localAddr")
      context.setReceiveTimeout(Duration.Undefined)
      context.become(connected(sender()))
  }

  /**
   * Register the connection handler upon accepted connection
   */
  def connected(tcpManager: ActorRef): Receive = LoggingReceive {
    case Tcp.Connected(remoteAddr, localAddr) =>
      log.debug(s"New connection from $remoteAddr accepted, processing...")
      val processor = system.actorOf(Props(classOf[DeviceDataProcessor], ProtoDeviceDataDecoder), name = genName)
      sender() ! Tcp.Register(processor)
  }

  private def genName = s"TcpConnectionProcessor_${connectionCounter.next()}"
}
