
# Setting up a "virtualized" Raspberry Pi environment

Original vagrant box copied from:
https://github.com/nickhutchinson/raspberry-devbox

Modifications:
- Vagrantfile: upgraded
- install-opam.sh: new file to install opam and oasis
- modules/raspberry_dev/files/rootfs/etc/puppet/modules/raspbian/manifests/init.pp: ocaml dev packages added

## Prerequisites

1. VirtualBox (`brew cask install virtualbox`)
2. Vagrant (`brew install vagrant`)

## Provisioning the vagrant box for cross-compilation

1. `vagrant up` (from directory containing *VagrantFile*).
2. Execute `./provision-ocaml.sh` from the same directory.

## Starting the box for cross-compilation

`vagrant up` (from directory containing *VagrantFile*).
