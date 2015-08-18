package milo.server

import java.net.InetSocketAddress

import akka.actor._
import akka.event.LoggingReceive
import akka.io.{IO, Tcp}

import scala.concurrent.duration.Duration

/**
 * Extremely basic implementation of Tcp server, that accepts incomming
 * connection requests, then creates a connection processor actor upon this connection.
 * TODO: fine grained tcp connection control, with back-pressure and fail-over
 */
final class MiloTcpServer(socket: InetSocketAddress) extends Actor with ActorLogging {
  import context.system

  private val connectionCounter = Iterator.from(0)

  /**
   * Register self as a server, listening for incomming Tcp connections.
   */
  IO(Tcp) ! Tcp.Bind(self, socket)

  override def receive: Receive = LoggingReceive {
    /**
     * When the actor is registered by the Tcp Manager, it accepts Tcp.Bound message,
     * signalling that this actor is ready to accept incomming tcp connections
     */
    case Tcp.Bound(localAddr) =>
      log.debug(s"MiloTcpServer is ready to listen for incomming connections on $localAddr")
      context.setReceiveTimeout(Duration.Undefined)
      context.become(connected(sender()))
  }

  /**
   * Register the connection handler upon accepted connection
   */
  def connected(tcpManager: ActorRef): Receive = LoggingReceive {
    case Tcp.Connected(remoteAddr, localAddr) =>
      log.debug(s"New connection from $remoteAddr accepted, processing...")
      system.actorOf(Props[DeviceDataProcessor], name = genName)
  }

  private def genName = s"TcpConnectionProcessor_${connectionCounter.next()}"
}
