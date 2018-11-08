cd ../MineriaDeDatos/PCA/

make clean
make

echo "******************************************************************************************************"
echo "Preparando Selector de Caracteristicas"
echo "******************************************************************************************************"

for input in ../../Data/*/*/*/*/vectorInicial/*
do
  echo "Ejecutando Selector de Caracteristicas : "$input
  path_output=$(echo $input | awk '{split($0, data, "vectorInicial"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	bolsaInput=$path_output"bolsa/"$output_file".bow"
	vectorSalida=$path_output"seleccion/final/"$output_file".vs"
	informationGain=$path_output"seleccion/informationGain/"$output_file".dat"
	forwardSelected=$path_output"seleccion/forwardSelected/"$output_file".dat"
	azar=$(echo $output_file | awk '{split($0, data, "-"); print data[1]}')
	archivoAzar=$path_output"instanciasAzar/"$azar".azar" 
	#python selector.py $input $bolsaInput ../../Data/Clases/clases.dat $vectorSalida $bolsaSalida $metricas $archivoAzar
	make run IN=$input	OUT=$vectorSalida BOLSA=$bolsaInput AZAR=$archivoAzar IG=$informationGain FS=$forwardSelected
	echo "********************************************************************************************************"
done

for input in ../../Data/*/*/*/vectorInicial/*
do
	echo "Ejecutando Selector de Caracteristicas : "$input
  path_output=$(echo $input | awk '{split($0, data, "vectorInicial"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/");  split(data[8], d, "."); print d[1]}')
	bolsaInput=$path_output"bolsa/"$output_file".bow"
  vectorSalida=$path_output"seleccion/final/"$output_file".vs"
	informationGain=$path_output"seleccion/informationGain/"$output_file".dat"
  forwardSelected=$path_output"seleccion/forwardSelected/"$output_file".dat"
	azar=$(echo $output_file | awk '{split($0, data, "-"); print data[1]}')
	archivoAzar=$path_output"instanciasAzar/"$azar".azar"
	#echo "python selector.py $input $bolsaInput ../../Data/Clases/clases.dat $vectorSalida $bolsaSalida $metricas $archivoAzar"
  #python selector.py $input $bolsaInput ../../Data/Clases/clases.dat $vectorSalida $bolsaSalida $metricas $archivoAzar
  make run IN=$input  OUT=$vectorSalida BOLSA=$bolsaInput AZAR=$archivoAzar IG=$informationGain FS=$forwardSelected
	echo "********************************************************************************************************"
done

cd ../../Controlador/

echo "Proceso Seleccion de Caracteristicas Terminado"
echo "******************************************************************************"
echo "******************************************************************************"
