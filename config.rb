# -*- mode: ruby -*- #

$docker_images = [
  {
    :image => "wurstmeister/zookeeper",
    :run => true,
    :args => "-p 2181:2181 " \
             "-p 2888:2888 " \
             "-p 3888:3888 " \
             "--name zookeeper"
  },
  {
    :image => "wurstmeister/kafka",
    :run => true,
    :args => "-p 9092:9092 " \
             "--name kafka " \
             "--link zookeeper " \
             "-e KAFKA_ADVERTISED_HOST_NAME='localhost' " \
             "-e KAFKA_BROKER_ID=1 " \
             "-e KAFKA_ZOOKEEPER_CONNECT=zookeeper " \
             "-e KAFKA_CREATE_TOPICS='ingest:1:1' " \
             "-v /var/run/docker.sock:/var/run/docker.sock"
  },
  {
    :image => "4lex1v/ingest:0.0.1",
    :run => true,
    :args => "--name ingest " \
             "-p 8080:8080 "
  }
  # {
  #   :image => "spotify/kafka",
  #   :run  => true,
  #   :args => "-p 2181:2181 " \
  #            "-p 9092:9092 " \
  #            "-e ADVERTISED_HOST=192.168.99.100 " \
  #            "-e ADVERTISED_PORT=9092 " \
  #            "-e TOPICS='ingest' " \
  #            "--name zkKafka"
  # }
]
