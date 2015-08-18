package milo

import akka.actor._
import akka.io.{IO, Tcp}
import akka.event.LoggingReceive
import java.net.InetSocketAddress
import scala.concurrent.duration.Duration

/**
 * Extremely basic implementation of Tcp server, that accepts incomming
 * connection requests, then creates a connection processor actor upon this connection.
 * No Ack/Nacks or any kind of backpressure handling at this moment.
 */
final class MiloTcpServer(socket: InetSocketAddress) extends Actor {
  import context.system

  // Just a simple shorthand
  private[this] val log = system.log

  /**
   * Regester self as a server, listenning for incomming Tcp connections.
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
      // Todo: add connection name counter
      system.actorOf(Props[TcpConnectionProcessor])
  }

}
