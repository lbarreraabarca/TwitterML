cd ../Preprocesamiento/LimpiarDatos/

echo "******************************************************************************************************"
echo "Compilacion..."
make clean
make
echo "******************************************************************************************************"


for input in ../../Data/*/*/*/*/json/*.json
do
	echo "Ejecutando Preprocesamiento : "$input
	path_output=$(echo $input | awk '{split($0, data, "json"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	make run IN=$input OUT=$path_output"csv/"$output_file".csv" STOPWORDS="../../Data/Stopwords/es/listado.stopwords"
	echo "********************************************************************************************************"
done

for input in ../../Data/*/*/*/json/*.json
do
	echo "Ejecutando Preprocesamiento : "$input
  path_output=$(echo $input | awk '{split($0, data, "json"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/");  split(data[8], d, "."); print d[1]}')
  make run IN=$input OUT=$path_output"csv/"$output_file".csv" STOPWORDS="../../Data/Stopwords/es/listado.stopwords"
	echo "********************************************************************************************************"
done 

cd ../../Controlador/

echo "Proceso Preprocesamiento Terminado"
echo "******************************************************************************"
echo "******************************************************************************"
