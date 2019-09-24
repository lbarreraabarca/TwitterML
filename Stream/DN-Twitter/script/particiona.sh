#!/bin/bash

# $1 : archivo de proxies
# $2 : numero de instancias

rm -fr tmp.out
cat $1 | sort -k3 -n | awk '{print $1 " " $2;}' > tmp.out

echo "$1"

cat tmp.out | awk -v N=$2 'BEGIN{
print N;
i = 0;
}
{
print "I=" i;
n = i % N;
print "n=" n " " N;
print $0 > "proxy" n ".dat"
i++;

}'
