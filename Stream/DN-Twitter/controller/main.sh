#!/bin/bash

#rm -fr prox* ok_proxies.dat tmp.out output* working_data/*
rm output* working_data/*


# PATH DE CONTROLLER
PCONTROL=/home/lbarrera/Documentos/MCC/2017-02/pruebas/DN-Twitter/controller

# NUMERO DE INSTANCIAS
N=5

# obtener listado de proxies
#cd /home/rac/Escritorio/practica/DN-Twitter/getProxiesJSOUP
#echo "cd /home/rac/Escritorio/practica/DN-Twitter/getProxiesJSOUP"

#make clean
#make
#make run
#mv /home/rac/Escritorio/practica/DN-Twitter/getProxiesJSOUP/proxies.dat $PCONTROL

# testing
#cd $PCONTROL
#mv proxies.dat proxies.test
#head -n 100 proxies.test > proxies.dat

# obtener proxies correctos
#cd /home/rac/Escritorio/practica/DN-Twitter/checkProxy
#echo "cd /home/rac/Escritorio/practica/DN-Twitter/checkProxy"

#make clean
#make
#make PROXYFILE=$PCONTROL/proxies.dat CTAFILE=$PCONTROL/cuenta_test.dat run
#mv /home/rac/Escritorio/practica/DN-Twitter/checkProxy/ok_proxies.dat $PCONTROL

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
