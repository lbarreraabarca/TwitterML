
echo "******************************************************************************"
echo "Uniendo conjunto de datos..."
cat ../Data/Nieve/2017/Jul/csv/* > ../Data/Nieve/2017/Jul/csv/tmp.csv
rm ../Data/Nieve/2017/Jul/csv/nieve*
echo "******************************************************************************"

cd ../Preprocesamiento/UTIL/eliminarRepetidos/

echo "******************************************************************************"
echo "Compilacion..."
make clean
make
echo "******************************************************************************"
make run in=../../../Data/Nieve/2017/Jul/csv/tmp.csv out=../../../Data/Nieve/2017/Jul/csv/nieve.csv
rm ../../../Data/Nieve/2017/Jul/csv/tmp.csv


cd ../../../Controlador/
echo "Proceso de Eliminacion de repetidos Terminado."
echo "******************************************************************************"
