
$nodes = 1

$docker_images = [
  "java:openjdk-8-jdk",
  "wurstmeister/zookeeper",
  "wurstmeister/kafka",
  "redis"
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
  end

  ## Plugin conflict
  if Vagrant.has_plugin?("vagrant-vbguest") then
    universe.vbguest.auto_update = false
  end

  ## Nodes configuration
  (1..$nodes).each do |i|
    universe.vm.define "core-%02d" % i do |box|
      
      ## Box network configuration
      box.vm.network "forwarded_port", guest: 2375, host: 2375, auto_correct: true
      box.vm.network :private_network, ip: "192.168.99.100"

      box.vm.provision "docker", images: $docker_images do |d|
        d.run "wurstmeister/zookeeper", demonize: true, args: "-p 2181:2181"
        d.run "wurstmeister/kafka", demonize: true, args: "-p 9092:9092 -e KAFKA_ADVERTISED_HOST_NAME=192.168.99.100 -e KAFKA_CREATE_TOPICS='ingest:1:1'"
      end

    end
  end

end
