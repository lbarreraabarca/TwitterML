#!/bin/bash

# PATH DE CONTROLLER
PCONTROL=/home/centos/dev/git/TwitterML/Stream/DN-Twitter/controller
cd $PCONTROL
echo "cd $PCONTROL"

rm output* working_data/*

# NUMERO DE INSTANCIAS
N=2

# obtener listado de proxies
JSOUP=/home/centos/dev/git/TwitterML/Stream/DN-Twitter/getProxiesJSOUP
cd $JSOUP
echo "cd $JSOUP"

make clean
make
make run
mv $JSOUP/proxies.dat $PCONTROL

# testing
cd $PCONTROL
mv proxies.dat proxies.test
head -n 10000 proxies.test > proxies.dat


# obtener proxies correctos
CK_PROX=/home/centos/dev/git/TwitterML/Stream/DN-Twitter/checkProxy
cp $PCONTROL/proxies.dat $CK_PROX/proxies.dat
cd $CK_PROX
echo "cd $CK_PROX"

make clean
make
make PROXYFILE=$PCONTROL/proxies.dat CTAFILE=$PCONTROL/cuenta_test.dat run
mv $CK_PROX/ok_proxies.dat $PCONTROL


PARTITION=/home/centos/dev/git/TwitterML/Stream/DN-Twitter/script
cd $PARTITION
echo "cd $PARTITION"

bash particiona.sh $PCONTROL/ok_proxies.dat $N
cp $PARTITION/proxy* $PCONTROL/
