#!/bin/bash

# */5 * * * * /home/fedora/sh/backup.sh ejecucion cada 5 min



N=5

while true
do 
	cd ../getProxiesJSOUP/
	make clean
	make
	make run
	cp ../getProxiesJSOUP/proxies.dat ../checkProxy

	cd ../checkProxy
	make clean
	make
	make run
	cp ../checkProxy/ok_proxies.dat ../controller

	cd ../controller
	sh particiona.sh ./ok_proxies.dat $N
	wc -l ok_proxies.dat >> c_ok_proxies.dat

	sleep 43200
done
