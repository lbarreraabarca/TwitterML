now="$(date)"
echo "[ generarVector ][ vectorizar.sh Inicio ][ "$now" ]"

echo "[ generarVector ][ cd ../MineriaDeDatos/generarVector/ ]"
cd ../MineriaDeDatos/generarVector/

echo "[ generarVector ][  Compilacion make ]"
make clean
make

largoVentana=$1	# este parametro es el que se debe variar para obtener los resultados. TW

# Terremoto 2017 y Huracan
for input in ../../Data/*/*/*/*/label/*
do
	echo "[ generarVector ][ 1 ][ Ejecutando generarVector ][ "$input" ]"
	path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"vectorInicial/"$output_file".vector" TS=$largoVentana VERBOS=../../Data/Palabras/Verbos/ConjugadosUnion/listadoverbos.verbo BOW=$path_output"bolsa/"$output_file"-bolsaPalabras.bow" BOH=$path_output"bolsa/"$output_file"-bolsaHashtags.bow" BUM=$path_output"bolsa/"$output_file"-bolsaUserMentions.bow" VEST=$path_output"vectorInicial/"$output_file"-estructurales.vector" VBOW=$path_output"vectorInicial/"$output_file"-bolsaPalabras.vector" VBOH=$path_output"vectorInicial/"$output_file"-bolsaHashtags.vector" VBUM=$path_output"vectorInicial/"$output_file"-bolsaUserMentions.vector"
	echo "[ generarVector ][ 1 ][ Ejecutando generarVector Terminada ][ "$input" ]"
done

# Nieve 2017 Stgo
for input in ../../Data/*/*/*/label/*
do
  echo "[ generarVector ][ 2 ][ Ejecutando generarVector ][ "$input" ]"
  path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[8], d, "."); print d[1]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"vectorInicial/"$output_file".vector" TS=$largoVentana VERBOS=../../Data/Palabras/Verbos/ConjugadosUnion/listadoverbos.verbo BOW=$path_output"bolsa/"$output_file"-bolsaPalabras.bow" BOH=$path_output"bolsa/"$output_file"-bolsaHashtags.bow" BUM=$path_output"bolsa/"$output_file"-bolsaUserMentions.bow" VEST=$path_output"vectorInicial/"$output_file"-estructurales.vector" VBOW=$path_output"vectorInicial/"$output_file"-bolsaPalabras.vector" VBOH=$path_output"vectorInicial/"$output_file"-bolsaHashtags.vector" VBUM=$path_output"vectorInicial/"$output_file"-bolsaUserMentions.vector"
	echo "[ generarVector ][ 2 ][ Ejecutando generarVector Terminada ][ "$input" ]"
done

#Terremoto 2014
for input in ../../Data/*/*/label/*
do
  echo "[ generarVector ][ 3 ][ Ejecutando generarVector ][ "$input" ]"
  path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[7], d, "."); print d[1]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"vectorInicial/"$output_file".vector" TS=$largoVentana VERBOS=../../Data/Palabras/Verbos/ConjugadosUnion/listadoverbos.verbo BOW=$path_output"bolsa/"$output_file"-bolsaPalabras.bow" BOH=$path_output"bolsa/"$output_file"-bolsaHashtags.bow" BUM=$path_output"bolsa/"$output_file"-bolsaUserMentions.bow" VEST=$path_output"vectorInicial/"$output_file"-estructurales.vector" VBOW=$path_output"vectorInicial/"$output_file"-bolsaPalabras.vector" VBOH=$path_output"vectorInicial/"$output_file"-bolsaHashtags.vector" VBUM=$path_output"vectorInicial/"$output_file"-bolsaUserMentions.vector"
	echo "[ generarVector ][ 3 ][ Ejecutando generarVector Terminada ][ "$input" ]"
done

#Volcano 2014
for input in ../../Data/EN/*/*/*/*/label/*
do
  echo "[ generarVector ][ 4 ][ Ejecutando generarVector ][ "$input" ]"
  path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[10], d, "."); print d[1]}')
	echo $output_file
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"vectorInicial/"$output_file".vector" TS=$largoVentana VERBOS=../../Data/Palabras/Verbos/ConjugadosUnion/listadoverbos.verbo BOW=$path_output"bolsa/"$output_file"-bolsaPalabras.bow" BOH=$path_output"bolsa/"$output_file"-bolsaHashtags.bow" BUM=$path_output"bolsa/"$output_file"-bolsaUserMentions.bow" VEST=$path_output"vectorInicial/"$output_file"-estructurales.vector" VBOW=$path_output"vectorInicial/"$output_file"-bolsaPalabras.vector" VBOH=$path_output"vectorInicial/"$output_file"-bolsaHashtags.vector" VBUM=$path_output"vectorInicial/"$output_file"-bolsaUserMentions.vector"
	echo "[ generarVector ][ 4 ][ Ejecutando generarVector Terminada ][ "$input" ]"
done

#Earthquake 2014
for input in ../../Data/EN/*/*/*/*/*/label/*
do
  echo "[ generarVector ][ 5 ][ Ejecutando generarVector ][ "$input" ]"
  path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[11], d, "."); print d[1]}')
	echo $output_file
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"vectorInicial/"$output_file".vector" TS=$largoVentana VERBOS=../../Data/Palabras/Verbos/ConjugadosUnion/listadoverbos.verbo BOW=$path_output"bolsa/"$output_file"-bolsaPalabras.bow" BOH=$path_output"bolsa/"$output_file"-bolsaHashtags.bow" BUM=$path_output"bolsa/"$output_file"-bolsaUserMentions.bow" VEST=$path_output"vectorInicial/"$output_file"-estructurales.vector" VBOW=$path_output"vectorInicial/"$output_file"-bolsaPalabras.vector" VBOH=$path_output"vectorInicial/"$output_file"-bolsaHashtags.vector" VBUM=$path_output"vectorInicial/"$output_file"-bolsaUserMentions.vector"
	echo "[ generarVector ][ 5 ][ Ejecutando generarVector Terminada ][ "$input" ]"
done


echo "[ generarVector ][ cd ../../Controlador/ ]"
cd ../../Controlador/
echo "[ generarVector ][ vectorizar.sh Terminada ][ "$now" ]"
