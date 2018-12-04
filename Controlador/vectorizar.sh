
cd ../MineriaDeDatos/generarVector/

echo "***************************************************************************************"
echo "Compilando Vectorizacion..."
make clean
make
echo "***************************************************************************************"
largoVentana=20

for input in ../../Data/*/*/*/*/label/*
do
	echo "Ejecutando Vectorizacion : "$input
	path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
	output_file=$(echo $input | awk '{split($0, data, "/"); split(data[9], d, "."); print d[1]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"vectorInicial/"$output_file".vector" TS=$largoVentana VERBOS=../../Data/Palabras/Verbos/ConjugadosUnion/listadoverbos.verbo BOW=$path_output"bolsa/"$output_file"-bolsaPalabras.bow" BOH=$path_output"bolsa/"$output_file"-bolsaHashtags.bow" BUM=$path_output"bolsa/"$output_file"-bolsaUserMentions.bow" VEST=$path_output"vectorInicial/"$output_file"-estructurales.vector" VBOW=$path_output"vectorInicial/"$output_file"-bolsaPalabras.vector" VBOH=$path_output"vectorInicial/"$output_file"-bolsaHashtags.vector" VBUM=$path_output"vectorInicial/"$output_file"-bolsaUserMentions.vector"
	echo "***************************************************************************************"
done


for input in ../../Data/*/*/*/label/*
do
  echo "Ejecutando Vectorizacion : "$input
  path_output=$(echo $input | awk '{split($0, data, "label"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "/"); split(data[8], d, "."); print d[1]}')
	make run IN=$input WORD=$path_output"keywords/keywords.word" OUT=$path_output"vectorInicial/"$output_file".vector" TS=$largoVentana VERBOS=../../Data/Palabras/Verbos/ConjugadosUnion/listadoverbos.verbo BOW=$path_output"bolsa/"$output_file"-bolsaPalabras.bow" BOH=$path_output"bolsa/"$output_file"-bolsaHashtags.bow" BUM=$path_output"bolsa/"$output_file"-bolsaUserMentions.bow" VEST=$path_output"vectorInicial/"$output_file"-estructurales.vector" VBOW=$path_output"vectorInicial/"$output_file"-bolsaPalabras.vector" VBOH=$path_output"vectorInicial/"$output_file"-bolsaHashtags.vector" VBUM=$path_output"vectorInicial/"$output_file"-bolsaUserMentions.vector"
	echo "***************************************************************************************"

done

cd ../../Controlador/

echo "Proceso de Vectorizacion Terminado."
echo "***************************************************************************************"
