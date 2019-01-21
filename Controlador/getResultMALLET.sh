now="$(date)"
echo "[ getResulMALLET ][ getResulMALLET.sh Inicio ][ "$now" ]"

cd ../MineriaDeDatos/Mallet/Twitter/Data

rm ../../../../Controlador/resultados.dat

for i in */vectorInicial/log/*
do
  accu=$(cat $i | grep "accuracy mean =")
  f1=$(cat $i | grep "f1")
  file=$(echo $i | awk '{split($0, data, "log"); print substr(data[2],2, length(data[2])-2)}')
  echo $file    >> ../../../../Controlador/resultados.dat
  echo $f1      >> ../../../../Controlador/resultados.dat
  echo $accu    >> ../../../../Controlador/resultados.dat
  echo "--------------------"   >> ../../../../Controlador/resultados.dat
done

cd ../../../../Controlador

cp resultados.dat "resultados/resultados-MALLET-TW-"$1".dat"

now="$(date)"
echo "[ getResulMALLET ][ getResulMALLET.sh Terminado ][ "$now" ]"



