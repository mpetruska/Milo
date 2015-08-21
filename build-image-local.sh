#! /bin/bash

sbt "project $1" docker:publishLocal
