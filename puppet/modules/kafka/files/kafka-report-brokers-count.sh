#!/bin/bash

ZOOKEEPER=$1
FREQ=$2
EXPIRE=$3

while true; do gmetric -n kafka.brokers.count -t uint8 -d $EXPIRE -v `zookeeper-shell.sh $ZOOKEEPER get /brokers/ids 2>&1 | grep -o 'numChildren = .*' | awk '{ print $3 }'`; sleep $FREQ; done