cd ../MineriaDeDatos/seleccionCaracteristicas/

echo "******************************************************************************************************"
echo "Preparando Selector de Caracteristicas"
echo "******************************************************************************************************"

for input in ../../Data/*/*/*/*/vectorInicial/*
do
  echo "Ejecutando Selector de Caracteristicas : "$input
  path_output=$(echo $input | awk '{split($0, data, "vectorInicial"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	bolsaInput=$path_output"bolsa/"$output_file".bow"
	vectorSalida=$path_output"seleccion/vectorSeleccionado/"$output_file".vs"
	bolsaSalida=$path_output"seleccion/bolsaSeleccionada/"$output_file".bs"
	metricas=$path_output"seleccion/metricas/"$output_file".metrics"
	azar=$(echo $output_file | awk '{split($0, data, "-"); print data[1]}')
	archivoAzar=$path_output"instanciasAzar/"$azar".azar" 
	echo "python selector.py $input $bolsaInput ../../Data/Clases/clases.dat $vectorSalida $bolsaSalida $metricas $archivoAzar"
	python selector.py $input $bolsaInput ../../Data/Clases/clases.dat $vectorSalida $bolsaSalida $metricas $archivoAzar
  echo "********************************************************************************************************"
done

for input in ../../Data/*/*/*/vectorInicial/*
do
	echo "Ejecutando Selector de Caracteristicas : "$input
  path_output=$(echo $input | awk '{split($0, data, "vectorInicial"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/");  split(data[8], d, "."); print d[1]}')
	bolsaInput=$path_output"bolsa/"$output_file".bow"
  vectorSalida=$path_output"seleccion/vectorSeleccionado/"$output_file".vs"
  bolsaSalida=$path_output"seleccion/bolsaSeleccionada/"$output_file".bs"
  metricas=$path_output"seleccion/metricas/"$output_file".metrics"
	azar=$(echo $output_file | awk '{split($0, data, "-"); print data[1]}')
	archivoAzar=$path_output"instanciasAzar/"$azar".azar"
	echo "python selector.py $input $bolsaInput ../../Data/Clases/clases.dat $vectorSalida $bolsaSalida $metricas $archivoAzar"
  python selector.py $input $bolsaInput ../../Data/Clases/clases.dat $vectorSalida $bolsaSalida $metricas $archivoAzar
  echo "********************************************************************************************************"
done

cd ../../Controlador/

echo "Proceso Seleccion de Caracteristicas Terminado"
echo "******************************************************************************"
echo "******************************************************************************"
