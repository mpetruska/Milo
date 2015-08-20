package milo.infrastructure

import akka.actor._
import com.softwaremill.react.kafka.{ConsumerProperties, ProducerProperties}
import com.typesafe.config.Config
import kafka.consumer.KafkaConsumer
import kafka.producer.KafkaProducer
import kafka.serializer.{Decoder, Encoder}

/**
 * Kafka Connection extension for akka actor systems. Reads the configuration values
 * from akka configuration and makes the availble in the context of the actor system
 * @param config Configuration
 */
class ReactiveKafkaConnectionImpl(config: Config) extends Extension {

  def producerProperties[T](topic: String)(implicit encoder: Encoder[T],
                                                          partitionizer: T => Option[Array[Byte]]) = {
    ProducerProperties(
      config.getString("kafka.broker.list"),
      topic,
      config.getString("kafka.client.id"),
      encoder,
      partitionizer)
  }

  def producerFor[T](topic: String)(implicit encoder: Encoder[T],
                                                partitionizer: T => Option[Array[Byte]] = (_: T) => None) =
    new KafkaProducer(producerProperties(topic))

  def consumerProperties[T](topic: String)(implicit decoder: Decoder[T]) =
    ConsumerProperties(
      config.getString("kafka.broker.list"),
      config.getString("kafka.consumer.zookeeper.connect"),
      topic,
      config.getString("kafka.client.id"),
      decoder)

  def consumerFor[T](topic: String)(implicit decoder: Decoder[T]) =
    new KafkaConsumer(consumerProperties(topic))
}

/**
 * Akka Extension provider implementation for ReactiveKafkaConnectionImpl
 */
object ReactiveKafkaConnectionProvider
  extends ExtensionId[ReactiveKafkaConnectionImpl]
  with ExtensionIdProvider {
  //The lookup method is required by ExtensionIdProvider,
  // so we return ourselves here, this allows us
  // to configure our extension to be loaded when
  // the ActorSystem starts up
  override def lookup = ReactiveKafkaConnectionProvider

  //This method will be called by Akka
  // to instantiate our Extension
  override def createExtension(system: ExtendedActorSystem) = new ReactiveKafkaConnectionImpl(system.settings.config)
}

/**
 * Convenience wrapper for actors to get access to kafka
 */
trait ReactiveKafkaConnection { self: Actor ⇒
  def producerFor[T](topic: String)(implicit encoder: Encoder[T],
                                                   partitionizer: T => Option[Array[Byte]] = (_: T) => None): KafkaProducer[T] =
    ReactiveKafkaConnectionProvider(context.system).producerFor[T](topic)

  def consumerFor[T](topic: String)(implicit decoder: Decoder[T]): KafkaConsumer[T] =
    ReactiveKafkaConnectionProvider(context.system).consumerFor[T](topic)
}