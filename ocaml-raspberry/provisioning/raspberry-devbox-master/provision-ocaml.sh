#!/bin/bash

vagrant ssh -c "cp /vagrant/install-opam.sh /home/vagrant/ && cp /vagrant/install-opam-packages.sh /home/vagrant/"
vagrant ssh -c "sb2 -t raspberry -eR puppet apply -e \"include raspbian\" && sb2 -t raspberry -eR ./install-opam.sh"
