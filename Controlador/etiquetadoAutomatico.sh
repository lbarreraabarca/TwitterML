now="$(date)"
echo "[ EtiquetadoAutomatico ][ etiquetadoAutomatico.sh Inicio ][ "$now" ]"

echo "[ EtiquetadoAutomatico ][ cd ../Preprocesamiento/EtiquetadoAutomatico/ ]"
cd ../Preprocesamiento/EtiquetadoAutomatico/
echo "[ EtiquetadoAutomatico ][ Compilacion make ]"
make clean;
make

#Terremoto 2017 y Huracan
for input in ../../Data/*/*/*/*/csv/*.csv
do
	echo "[ EtiquetadoAutomatico ][ 1 ][ Ejecutando EtiquetadoAutomatico ][ "$input" ]"
	path_output=$(echo $input | awk '{split($0, data, "csv"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	tsmin=$(cat $path_output"horaCrisis/tsMinutos.tm")
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"label/"$output_file".csv" TS=$(cat $path_output"horaCrisis/tsMinutos.tm") TW="5" TSF=$path_output"frecuenciaSerie/analisis-hora/frecuencia/serie" TSM=$path_output"frecuenciaSerie/analisis-hora/modelo/model"
	echo "[ EtiquetadoAutomatico ][ 1 ][ Ejecucion EtiquetadoAutomatico Terminada ][ "$input" ]"
done

#Nieve
for input in ../../Data/*/*/*/csv/*.csv
do
 echo "[ EtiquetadoAutomatico ][ 2 ][ Ejecutando EtiquetadoAutomatico ][ "$input" ]"
 path_output=$(echo $input | awk '{split($0, data, "csv"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "csv"); print data[2]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"label/"$output_file".csv" TS=$(cat $path_output"horaCrisis/tsMinutos.tm") TW="5" TSF=$path_output"frecuenciaSerie/analisis-hora/frecuencia/serie" TSM=$path_output"frecuenciaSerie/analisis-hora/modelo/model"
	echo "[ EtiquetadoAutomatico ][ 2 ][ Ejecucion EtiquetadoAutomatico Terminada ][ "$input" ]"
done

# Terremoto 2014
for input in ../../Data/*/*/csv/*.csv
do
 echo "[ EtiquetadoAutomatico ][ 3 ][ Ejecutando EtiquetadoAutomatico ][ "$input" ]"
 path_output=$(echo $input | awk '{split($0, data, "csv"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "csv"); print data[2]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"label/"$output_file".csv" TS=$(cat $path_output"horaCrisis/tsMinutos.tm") TW="5" TSF=$path_output"frecuenciaSerie/analisis-hora/frecuencia/serie" TSM=$path_output"frecuenciaSerie/analisis-hora/modelo/model"
	echo "[ EtiquetadoAutomatico ][ 3 ][ Ejecucion EtiquetadoAutomatico Terminada ][ "$input" ]"
done

# Volcano 2014
for input in ../../Data/EN/*/*/*/*/csv/*.csv
do
  echo "[ EtiquetadoAutomatico ][ 4 ][ Ejecutando EtiquetadoAutomatico ][ "$input" ]"
  path_output=$(echo $input | awk '{split($0, data, "csv"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "csv"); print data[2]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"label/"$output_file".csv" TS=$(cat $path_output"horaCrisis/tsMinutos.tm") TW="5" TSF=$path_output"frecuenciaSerie/analisis-hora/frecuencia/serie" TSM=$path_output"frecuenciaSerie/analisis-hora/modelo/model"
	echo "[ EtiquetadoAutomatico ][ 4 ][ Ejecucion EtiquetadoAutomatico Terminada ][ "$input" ]"
done

# Earthquake 2014
for input in ../../Data/EN/*/*/*/*/*/csv/*.csv
do
  echo "[ EtiquetadoAutomatico ][ 5 ][ Ejecutando EtiquetadoAutomatico ][ "$input" ]"
  path_output=$(echo $input | awk '{split($0, data, "csv"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "csv"); print data[2]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"label/"$output_file".csv" TS=$(cat $path_output"horaCrisis/tsMinutos.tm") TW="5" TSF=$path_output"frecuenciaSerie/analisis-hora/frecuencia/serie" TSM=$path_output"frecuenciaSerie/analisis-hora/modelo/model"
	echo "[ EtiquetadoAutomatico ][ 5 ][ Ejecucion EtiquetadoAutomatico Terminada ][ "$input" ]"
done

now="$(date)"
cd ../../Controlador/
echo "[ EtiquetadoAutomatico ][ cd ../../Controlador/ ]"
echo "[ EtiquetadoAutomatico ][ etiquetadoAutomatico.sh Terminada ][ "$now" ]"
