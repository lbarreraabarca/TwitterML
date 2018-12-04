
cd ../Preprocesamiento/EtiquetadoAutomatico/
echo "*************************************************************************************************"
echo "Compilando Etiquetado Automatico..."
make clean;
make
echo "*************************************************************************************************"

for input in ../../Data/*/*/*/*/csv/*.csv
do
	echo "Ejecutando : "$input
	path_output=$(echo $input | awk '{split($0, data, "csv"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	tsmin=$(cat $path_output"horaCrisis/tsMinutos.tm")
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"label/"$output_file".csv" TS=$(cat $path_output"horaCrisis/tsMinutos.tm") TW="5" TSF=$path_output"frecuenciaSerie/analisis-hora/frecuencia/serie" TSM=$path_output"frecuenciaSerie/analisis-hora/modelo/model"
	echo "*************************************************************************************************"
done


for input in ../../Data/*/*/*/csv/*.csv
do
  echo "Ejecutando : "$input
  path_output=$(echo $input | awk '{split($0, data, "csv"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "csv"); print data[2]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"label/"$output_file".csv" TS=$(cat $path_output"horaCrisis/tsMinutos.tm") TW="5" TSF=$path_output"frecuenciaSerie/analisis-hora/frecuencia/serie" TSM=$path_output"frecuenciaSerie/analisis-hora/modelo/model"
	echo "*************************************************************************************************"
done


cd ../../Controlador/

echo "Proceso de Etiquetado automatico Finalizado..."
echo "*************************************************************************************************"

