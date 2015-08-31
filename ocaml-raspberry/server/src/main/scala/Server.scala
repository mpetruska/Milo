import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import akka.actor.{Cancellable, Props, Actor}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

class Server extends Actor {

  import Tcp._
  import context.system

  val port = ConfigFactory.load().getInt("tcp.port")

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", port))

  def receive = {
    case 'Start =>
      println("Starting...")

    case Bound(localAddress) =>
      println(s"Waiting for connections to $localAddress")

    case CommandFailed(_: Bind) =>
      println("Could not bind to address, exiting...")
      context.system.shutdown()

    case Connected(remote, local) =>
      println(s"Incoming connection from $remote.")
      val handler = context.actorOf(LoggingHandler.props(remote))
      val connection = sender()
      connection ! Register(handler)
  }

}

object LoggingHandler {

  def props(remote: InetSocketAddress) = {
    Props(new LoggingHandler(remote))
  }

}

class LoggingHandler(remote: InetSocketAddress) extends Actor {

  import Tcp._

  val timeout = FiniteDuration(5L, TimeUnit.SECONDS)
  var timeoutSchedule: Option[Cancellable] = None
  scheduleTimeout()

  def receive = {

    case Received(data) =>
      scheduleTimeout()
      log(data)

    case 'Timeout =>
      println(s"Timeout, closing connection to $remote")
      stop()

    case PeerClosed =>
      println(s"Closing connection to $remote")
      stop()

  }

  def log(data: ByteString) = {
    println(s"Received data from $remote: $data")
  }

  def cancelTimeout() = {
    timeoutSchedule map { c => c.cancel() }
  }

  def scheduleTimeout() = {
    import scala.concurrent.ExecutionContext.Implicits.global

    cancelTimeout()
    timeoutSchedule = Some(context.system.scheduler.scheduleOnce(timeout, self, 'Timeout))
  }

  def stop() = {
    cancelTimeout()
    context stop self
  }

}
