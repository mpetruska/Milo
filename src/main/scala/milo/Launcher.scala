package milo

import akka.actor._
import com.typesafe.config.ConfigFactory
import java.net.InetSocketAddress

object Launcher {
  implicit val universe = ActorSystem("MiloSystem")

  // Application configuration file, by default
  // should be 'application.conf'
  val config = ConfigFactory.load()

  // Application entry point
  def main(args: Array[String]): Unit = {
  
    val interface = config.getString("interface")
    val port      = config.getInt("port")

    val socket = new InetSocketAddress(interface, port)

    universe.actorOf(Props(classOf[MiloTcpServer], socket))
  }
}
