package milo.server

import akka.actor._
import akka.io._
import akka.testkit.TestProbe
import java.net.InetSocketAddress

import org.scalatest._

final class MiloTcpServerSpec
    extends WordSpec
    with Matchers {

  implicit val system = ActorSystem("TestMiloSystem")

  "Ingest tcp server infrastructure" should {
    
  }

  class Infrastructure {
    val socket = new InetSocketAddress("localhost", 8080)
    val bindHandler = TestProbe()

    val server = {
      val commander = TestProbe()
      commander.send(IO(Tcp), Tcp.Bind(bindHandler.ref, socket))
      commander.expectMsgType[Tcp.Bound]
      commander.sender
    }

  }

}


