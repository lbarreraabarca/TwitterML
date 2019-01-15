now="$(date)"
echo "[ main ][ main.sh Inicio ][ "$now" ]"

#./preprocesar.sh
#./eliminarRepetidos.sh
#./etiquetadoAutomatico.sh
./vectorizar.sh
./convertSVMformat.sh
./divideTrainTestSVM.sh
###./obtenerInstanciasAzar.sh
###./PCA.sh
#./modelar.sh
#./modelarSVM.sh

now="$(date)"
echo "[ main ][ main.sh Terminado ][ "$now" ]"
