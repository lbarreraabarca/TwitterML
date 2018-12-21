now="$(date)"
echo "[ LimpiarDatos ][ preprocesamiento.sh Inicio ][ "$now" ]"

cd ../Preprocesamiento/LimpiarDatos/

echo "[ LimpiarDatos ][ cd ../Preprocesamiento/LimpiarDatos/ ]"
echo "[ LimpiarDatos ][ Compilacion make ]"
make clean
make

#Terremoto 2017 y Huracanes
# for input in ../../Data/*/*/*/*/json/*.json
# do
	# echo "[ LimpiarDatos ][ 1 ][ Ejecutando LimpiarDatos ][ "$input" ]"
	# path_output=$(echo $input | awk '{split($0, data, "json"); print data[1]}')
	# output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	# make run IN=$input OUT=$path_output"csv/"$output_file".csv" STOPWORDS="../../Data/Stopwords/es/listado.stopwords"
	# echo "[ LimpiarDatos ][ 1 ][ Ejecucion LimpiarDatos Terminada ][ "$input" ]"
# done

# Nieve
# for input in ../../Data/*/*/*/json/*.json
# do
	# echo "[ LimpiarDatos ][ 2 ][ Ejecutando LimpiarDatos ][ "$input" ]"
 # path_output=$(echo $input | awk '{split($0, data, "json"); print data[1]}')
 # output_file=$(echo $input | awk '{split($0, data, "/");  split(data[8], d, "."); print d[1]}')
 # make run IN=$input OUT=$path_output"csv/"$output_file".csv" STOPWORDS="../../Data/Stopwords/es/listado.stopwords"
	# echo "[ LimpiarDatos ][ 2 ][ Ejecucion LimpiarDatos Terminada ][ "$input" ]"
# done

cd ../../Controlador/
echo "[ LimpiarDatosV2 ][ cd ../../Controlador/ ]"
cd ../Preprocesamiento/LimpiarDatosV2/
echo "[ LimpiarDatosV2 ][ cd ../Preprocesamiento/LimpiarDatosV2/ ]"
echo "[ LimpiarDatosV2 ][ Compilacion make ]"
make clean
make

#Terremoto 2014
# for input in ../../Data/*/*/json/*.json
# do
	# echo "[ LimpiarDatosV2 ][ 3 ][ Ejecutando LimpiarDatosV2 ][ "$input" ]"
	# path_output=$(echo $input | awk '{split($0, data, "json"); print data[1]}')
	# output_file=$(echo $input | awk '{split($0, data, "/"); split(data[7], d, "."); print d[1]}')
	# make run IN=$input OUT=$path_output"csv/"$output_file".csv" STOPWORDS="../../Data/Stopwords/es/listado.stopwords"
	# echo "[ LimpiarDatosV2 ][ 3 ][ Ejecucion LimpiarDatosV2 Terminada ][ "$input" ]"
# done

cd ../../Controlador/
echo "[ LimpiarDatosEN ][ cd ../../Controlador/ ]"
cd ../Preprocesamiento/LimpiarDatosEN/
echo "[ LimpiarDatosEN ][ cd ../Preprocesamiento/LimpiarDatosEN/ ]"
echo "[ LimpiarDatosEN ][ Compilacion make ]"
make clean
make


#Volcano 2014
for input in ../../Data/EN/*/*/*/*/json/*.json
do
	echo "[ LimpiarDatosEN ][ 4 ][ Ejecutando LimpiarDatosEN ][ "$input" ]"
	path_output=$(echo $input | awk '{split($0, data, "json"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "/"); split(data[10], d, "."); print d[1]}')
	make run IN=$input OUT=$path_output"csv/"$output_file".csv" STOPWORDS="../../Data/Stopwords/en/listado.stopwords"
	echo "[ LimpiarDatosEN ][ 4 ][ Ejecucion LimpiarDatosEN Terminada ][ "$input" ]"
done

now="$(date)"
cd ../../Controlador/
echo "[ LimpiarDatos ][ cd ../../Controlador/ ]"
echo "[ LimpiarDatos ][ preprocesamiento.sh Terminado ][ "$now" ]"
