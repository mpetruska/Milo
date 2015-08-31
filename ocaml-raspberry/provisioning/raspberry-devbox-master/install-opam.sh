#!/bin/bash

cd /home/vagrant
mkdir git
cd git

git clone https://github.com/ocaml/opam.git
cd opam

./configure
make lib-ext
make
make install

opam init --yes
opam switch 4.02.3
../../install-opam-packages.sh
