package milo.server

import java.net.InetSocketAddress
import akka.actor._
import akka.io._

import org.scalatest._

import milo.server._

trait MiloServerInfrastructure extends BeforeAndAfterAll { this: Suite =>
  implicit def system: ActorSystem

  val socket = new InetSocketAddress("localhost", 8080)

  override def beforeAll() = {
    system.actorOf(Props(classOf[MiloTcpServer], socket))
  }

  override def afterAll() = {
    system.shutdown()
  }
  
}
