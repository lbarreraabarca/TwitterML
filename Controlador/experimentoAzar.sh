
cd ../MineriaDeDatos/UTIL/experimentoAzarEtiquetado

echo "***************************************************************************************"
echo "Compilando Vectorizacion..."
make clean
make
echo "***************************************************************************************"
largoVentana=5

for input in ../../../Data/*/*/*/*/label/*
do
	echo "Ejecutando Vectorizacion : "$input
	path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	salida=$path_output"experimentoAzar/"$output_file
	make run IN=$input OUT=$salida
	echo "***************************************************************************************"
done


for input in ../../../Data/*/*/*/label/*
do
  echo "Ejecutando Vectorizacion : "$input
  path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[8], d, "."); print d[1]}')
	salida=$path_output"experimentoAzar/"$output_file
	make run IN=$input OUT=$salida	
	echo "***************************************************************************************"

done

cd ../../../Controlador/

echo "Proceso de Vectorizacion Terminado."
echo "***************************************************************************************"
