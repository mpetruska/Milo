#!/bin/bash

VAGRANT_DIR=provisioning/raspberry-devbox-master

mkdir -p $VAGRANT_DIR/app/src
mkdir -p $VAGRANT_DIR/app/test
cp -f app/_oasis $VAGRANT_DIR/app/
cp -f app/build.sh $VAGRANT_DIR/app/
cp -f app/test.sh $VAGRANT_DIR/app/
cp -rf app/src/* $VAGRANT_DIR/app/src/
cp -rf app/test/* $VAGRANT_DIR/app/test/

cd $VAGRANT_DIR

vagrant ssh -c "cp -rf /vagrant/app /home/vagrant/ && echo \"cd /home/vagrant/app && ./test.sh\" | sb2 -eR"
