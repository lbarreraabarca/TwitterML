
cd ../MineriaDeDatos/UTIL/obtenerTuitsAzar/
echo "***************************************************************************************"
echo "Compilando Azar..."
make clean
make
echo "***************************************************************************************"


for input in ../../../Data/*/*/*/*/vectorInicial/*
do
  echo "Ejecutando Selector de Caracteristicas : "$input
  path_output=$(echo $input | awk '{split($0, data, "vectorInicial"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[10], d, "."); split( d[1], a, "-"); print a[1]}')
	estructurales=$(echo $input | grep estructurales | wc -l )
	if [ $estructurales -eq 1 ]; then
		salida=$path_output"instanciasAzar/"$output_file."azar"
		make run IN=$input OUT=$salida
	fi
  echo "********************************************************************************************************"
done

for input in ../../../Data/*/*/*/vectorInicial/*
do
	echo "Ejecutando Selector de Caracteristicas : "$input
  path_output=$(echo $input | awk '{split($0, data, "vectorInicial"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/");  split(data[9], d, "."); split( d[1], a, "-"); print a[1]}')
	estructurales=$(echo $input | grep estructurales | wc -l )
  if [ $estructurales -eq 1 ]; then
		salida=$path_output"instanciasAzar/"$output_file."azar"
		make run IN=$input OUT=$salida
  fi
  echo "********************************************************************************************************"
done

cd ../../../Controlador/
