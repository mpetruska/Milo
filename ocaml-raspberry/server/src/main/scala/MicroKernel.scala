import akka.actor.{Props, ActorSystem}

object MicroKernel {

  val system = ActorSystem("tcp-server-sample")

  def main(args: Array[String]) = {
    system.actorOf(Props[Server]) ! 'Start
    println("Running, CTRL+C to exit")
    system.awaitTermination()
  }

}
