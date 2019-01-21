now="$(date)"
echo "[ getResultSVM ][ getResultSVM.sh Inicio ][ "$now" ]"

cd ../MineriaDeDatos/SVM-Light/Twitter/Data/

rm ../../../../Controlador/resultadosSVM.dat

for i in */result/result.dat
do
  accu=$(cat $i | grep "Accuracy")
  f1=$(cat $i | grep "F1")
  file=$(echo $i | awk '{split($0, data, "result"); print data[1]}')
  
  echo $file     >> ../../../../Controlador/resultadosSVM.dat
  echo $f1       >> ../../../../Controlador/resultadosSVM.dat
  echo $accu     >> ../../../../Controlador/resultadosSVM.dat
  echo "--------------------"  >> ../../../../Controlador/resultadosSVM.dat
done

cd ../../../../Controlador

cp resultadosSVM.dat "resultados/resultados-SVM-TW-"$1".dat"

now="$(date)"
echo "[ getResultSVM ][ getResultSVM.sh Terminado ][ "$now" ]"