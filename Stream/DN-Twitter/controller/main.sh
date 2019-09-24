#!/bin/bash
# PATH DE CONTROLLER
PCONTROL=/home/centos/dev/git/TwitterML/Stream/DN-Twitter/controller
cd $PCONTROL
echo "cd $PCONTROL"

rm output* working_data/*

# NUMERO DE INSTANCIAS
N=5

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
head -n 10 proxies.test > proxies.dat

# obtener proxies correctos
CK_PROX=/home/centos/dev/git/TwitterML/Stream/DN-Twitter/checkProxy
cd $CK_PROX
echo "cd $CK_PROX"

make clean
make
make PROXYFILE=$PCONTROL/proxies.dat CTAFILE=$PCONTROL/cuenta_test.dat run
mv $CK_PROX/ok_proxies.dat $PCONTROL

# particionar los proxies correctos
cd $PCONTROL
echo "cd $PCONTROL"


# comenzar la descarga
cd /home/lbarrera/PROYECTOS/PRACTICA-201701-carmona-cifuentes/DN-Twitter/twitter_proxy
echo "cd /home/lbarrera/PROYECTOS/PRACTICA-201701-carmona-cifuentes/DN-Twitter/twitter_proxy"

make clean
make

FILETERMS=$PCONTROL/terminos.dat
FILECUENTAS=$PCONTROL/cuentas.dat
for n in $(seq 0 $(echo "$N-1" | bc -l)); do
  echo "INSTANCIA=$n"

  FILEOUT=$PCONTROL/working_data/data$n.dat
  FILEPROXY=$PCONTROL/proxy$n.dat
  FILE=$PCONTROL/output$n.out
  sh run.sh $n $n $FILEOUT $FILEPROXY $FILETERMS $FILECUENTAS > $FILE &
done
