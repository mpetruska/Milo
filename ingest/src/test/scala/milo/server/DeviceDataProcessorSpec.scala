package milo.server

import java.net.InetSocketAddress
import akka.actor._
import akka.io._
import akka.testkit._
import org.scalatest._


final class DeviceDataProcessorSpec
  extends WordSpec
     with Matchers {

  implicit val system = ActorSystem("Test")

//  "Per connection data processor" should {
//
//    "create a device data decoder when the device initiate the connection" in new Infrastructure {
//      val device    = DeviceEmulator(socket)
//      val commander = TestProbe()
//
//      commander.
//
//    }
//
//  }

  class Infrastructure {
    val socket = new InetSocketAddress("localhost", 8080)
  }

}
