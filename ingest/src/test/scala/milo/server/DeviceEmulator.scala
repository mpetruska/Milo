package milo.server

import java.net.InetSocketAddress
import akka.actor._
import akka.io._

import org.scalacheck._

/**
 * Sends generated data over tcp connection to the data processor on the
 * other end. Should suffice the following requirements:
 *   - Be configurable
 *   - Be controlable (send one message, multiple generated messages)
 */
final class DeviceEmulator(socket: InetSocketAddress) extends Actor {
  import context.system

  IO(Tcp) ! Tcp.Connect(socket)

}

object DeviceEmulator {
  def props(socket: InetSocketAddress): Props = 
    Props(classOf[DeviceEmulator], socket)
}
