package milo.launcher

import java.net.InetSocketAddress

import akka.actor._
import com.typesafe.config.ConfigFactory
import milo.server.MiloTcpServer

object Launcher {
  // Application configuration file, by default
  // should be 'application.conf'
  val config = ConfigFactory.load()

  implicit val universe = ActorSystem("MiloSystem", config)

  val miloConfig = config.getConfig("milo.server")

  // Application entry point
  def main(args: Array[String]): Unit = {
    val interface = miloConfig.getString("interface")
    val port      = miloConfig.getInt("port")

    val socket = new InetSocketAddress(interface, port)

    universe.actorOf(Props(classOf[MiloTcpServer], socket))
    
  }

}
