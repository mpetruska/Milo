# -*- mode: ruby -*- #

require 'fileutils'

Vagrant.require_version ">= 1.7.0"

CONFIG        = File.join(File.dirname(__FILE__), "config.rb")
COREOS_CONFIG = File.join(File.dirname(__FILE__), "coreos-config")

## Default configuration
## (Can be overriden in CONFIG)
$nodes         = 1
$docker_images = []

if File.exist?(CONFIG)
  require CONFIG
end

Vagrant.configure("2") do |universe|

  ## Essential CoreOS box environment configuration
  universe.vm.box         = "coreos-alpha"
  universe.vm.box_version = ">= 744.0.0"
  universe.vm.box_url     = "http://alpha.release.core-os.net/amd64-usr/current/coreos_production_vagrant.json"

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

      ## TODO :: This should be fixed to avoid port collision and having access to all
      ##         applications in all nodes (increment by node number, as an offset)
      box.vm.network "forwarded_port", guest: 2375, host: 2375, auto_correct: true ## Some docker shit
      box.vm.network "forwarded_port", guest: 2181, host: 2181, auto_correct: true ## ZooKeeper
      box.vm.network "forwarded_port", guest: 9092, host: 9092, auto_correct: true ## Kafka
      box.vm.network "forwarded_port", guest: 9160, host: 9160, auto_correct: true ## Cassandra, Thrift
      box.vm.network "forwarded_port", guest: 9042, host: 9042, auto_correct: true ## Cassandra, Native protocol

      box.vm.provision "docker" do |d|
        $docker_images.each do |image|
          d.pull_images image[:image]
          if image[:run] then
            d.run image[:image],
                  daemonize: true,
                  args: image[:args]
          end
        end
      end ## provision docker endb

      if File.exist?(COREOS_CONFIG)
        config.vm.provision :file, :source => "#{COREOS_CONFIG}", :destination => "/tmp/vagrantfile-user-data"
        config.vm.provision :shell, :inline => "mv /tmp/vagrantfile-user-data /var/lib/coreos-vagrant/", :privileged => true
      end

    end ## box configuration end

  end ## node configuration end

end ## vagrant configuration end
