
$nodes = 1

$docker_images = [
  {
    :image => "java:openjdk-8-jdk",
    :run  => false
  },
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
             "-e KAFKA_ADVERTISED_HOST_NAME='192.168.99.100' " \
             "-e KAFKA_BROKER_ID=1 " \
             "-e KAFKA_ZOOKEEPER_CONNECT=zookeeper " \
             "-e KAFKA_CREATE_TOPICS='ingest:1:1' " \
             "-v /var/run/docker.sock:/var/run/docker.sock"
             
  }  
  # {
  #   :name => "jplock/zookeeper",
  #   :run  => true,
  #   :args => "-p 2181:2181 " \
  #            "-p 2888:2888 " \
  #            "-p 3888:3888 "
  # },  
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

Vagrant.configure("2") do |universe|

  ## Essential CoreOS box environment configuration
  universe.vm.box = "coreos-alpha"
  universe.vm.box_version = ">= 744.0.0"
  universe.vm.box_url = "http://alpha.release.core-os.net/amd64-usr/current/coreos_production_vagrant.json"

  universe.vm.provider :virtualbox do |v|
    # On VirtualBox, we don't have guest additions or a functional vboxsf
    # in CoreOS, so tell Vagrant that so it can be smarter.
    v.check_guest_additions = false
    v.functional_vboxsf     = false

    v.customize  ["modifyvm", :id, "--cpus", 4]
    v.customize  ["modifyvm", :id, "--memory", 6144]
  end

  ## Plugin conflict
  if Vagrant.has_plugin?("vagrant-vbguest") then
    universe.vbguest.auto_update = false
  end

  ## Nodes configuration
  (1..$nodes).each do |i|
    universe.vm.define "core-%02d" % i do |box|
      
      ## Box network configuration ##
      box.vm.network :private_network, ip: "192.168.99.100"

      box.vm.network "forwarded_port", guest: 2375, host: 2375, auto_correct: true ## Some docker shit
      box.vm.network "forwarded_port", guest: 2181, host: 2181, auto_correct: true ## ZooKeeper
      box.vm.network "forwarded_port", guest: 9092, host: 9092, auto_correct: true ## Kafka

      box.vm.provision "docker" do |d|
        $docker_images.each do |image|
          d.pull_images image[:image]
          if image[:run] then
            d.run image[:image],
                  daemonize: true,
                  args: image[:args]
          end
        end
      end

    end
  end

end
