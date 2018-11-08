#!/bin/bash
#rm -fr prox* ok_proxies.dat tmp.out output* working_data/*
rm output* working_data/*

# PATH DE CONTROLLER
PCONTROL=/home/lbarrera/PROYECTOS/PRACTICA-201701-carmona-cifuentes/DN-Twitter/controller

# NUMERO DE INSTANCIAS
N=1

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
  FILEPROXY=$PCONTROL/proxy_test.dat
  FILE=$PCONTROL/output_test.out
  sh run.sh $n $n $FILEOUT $FILEPROXY $FILETERMS $FILECUENTAS > $FILE &
done
