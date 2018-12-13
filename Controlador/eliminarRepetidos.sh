echo "[ eliminarRepetidos ][ eliminarRepetidos.sh Inicio ]"

echo "[ eliminarRepetidos ][ Join Data ][ Nieve ]"
echo "[ eliminarRepetidos ][ cat ../Data/Nieve/2017/Jul/csv/* > ../Data/Nieve/2017/Jul/csv/tmp.csv ]"
cat ../Data/Nieve/2017/Jul/csv/* > ../Data/Nieve/2017/Jul/csv/tmp.csv
echo "[ eliminarRepetidos ][ rm ../Data/Nieve/2017/Jul/csv/nieve* ]"
rm ../Data/Nieve/2017/Jul/csv/nieve*

echo "[ eliminarRepetidos ][ cd ../Preprocesamiento/UTIL/eliminarRepetidos/ ]"
cd ../Preprocesamiento/UTIL/eliminarRepetidos/

echo "[ eliminarRepetidos ][ Compilacion make ]"
make clean
make

#Ejecutar para eliminar repetidos de Nieve
echo "[ eliminarRepetidos ][ 1 ][ Ejecutando eliminarRepetidos ][ Nieve ][ ../../../Data/Nieve/2017/Jul/csv/tmp.csv ]"
make run in=../../../Data/Nieve/2017/Jul/csv/tmp.csv out=../../../Data/Nieve/2017/Jul/csv/nieve.csv
echo "[ eliminarRepetidos ][ rm ../../../Data/Nieve/2017/Jul/csv/tmp.csv ]"
rm ../../../Data/Nieve/2017/Jul/csv/tmp.csv


#Ejecutar para eliminar repetidos de Terremoto 2014
echo "[ eliminarRepetidos ][ 2 ][ Ejecutando eliminarRepetidos ][ Terremoto 2014 ][ ../../../Data/Terremotos/2014/csv/terremoto-chile2014.csv ]"
cat ../../../Data/Terremotos/2014/csv/terremoto-chile2014.csv | sort | uniq -c | awk '{ print substr($0, 9, length($0)) }' > ../../../Data/Terremotos/2014/csv/tmp.csv
echo "[ eliminarRepetidos ][ mv ../../../Data/Terremotos/2014/csv/tmp.csv ../../../Data/Terremotos/2014/csv/terremoto-chile2014.csv ]"
mv ../../../Data/Terremotos/2014/csv/tmp.csv ../../../Data/Terremotos/2014/csv/terremoto-chile2014.csv

cd ../../../Controlador/
echo "[ eliminarRepetidos ][ cd ../../../Controlador/ ]"
echo "[ eliminarRepetidos ][ eliminarRepetidos.sh Terminado ]"
