#!/bin/bash

eval `opam config env`
oasis setup
ocaml setup.ml -configure --enable-tests > /dev/null
ocaml setup.ml -build
ocaml setup.ml -test
