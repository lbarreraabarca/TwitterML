now="$(date)"
echo "[ modelar ][ modelar.sh Inicio ][ "$now" ]"

#Huracanes
echo "[ modelar ][ Copy Data ][ Huracan ][ Irma ]"
cp -fr ../Data/Huracanes/2017/Sept/Irma/vectorInicial/* ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorInicial/
#cp -fr ../Data/Huracanes/2017/Sept/Irma/vectorFinal/* ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorFinal/

#Terremotos
echo "[ modelar ][ Copy Data ][ Terremoto ][ 2017 ]"
cp -fr ../Data/Terremotos/2017/Sept/07/vectorInicial/* ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorInicial/
# Feat Selection # cp -fr ../Data/Terremotos/2017/Sept/07/vectorFinal/* ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorFinal/

#Nieve
echo "[ modelar ][ Copy Data ][ Nieve ][ 2017 ]"
cp -fr ../Data/Nieve/2017/Jul/vectorInicial/* ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorInicial/
# Feat Selection #cp -fr ../Data/Nieve/2017/Jul/vectorFinal/* ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorFinal/

#Terremoto 2014
echo "[ modelar ][ Copy Data ][ Terremoto ][ 2014 ]"
cp -fr ../Data/Terremotos/2014/vectorInicial/* ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto_2014/vectorInicial/

#Volcano 2014
echo "[ modelar ][ Copy Data ][ Volcano ][ 2014 ]"
cp -fr ../Data/EN/Event/Volcano/2014/ago/vectorInicial/* ../MineriaDeDatos/Mallet/Twitter/Data/Volcano/vectorInicial/



echo "[ modelar ][ Delete Data ][ bolsaUserMentions ]"
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorInicial/*bolsaUserMentions*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorInicial/*bolsaUserMentions*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorInicial/*bolsaUserMentions*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto_2014/vectorInicial/*bolsaUserMentions*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Volcano/vectorInicial/*bolsaUserMentions*

echo "[ modelar ][ Delete Data ][ bolsaHashtags ]"
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorInicial/*bolsaHashtags*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorInicial/*bolsaHashtags*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorInicial/*bolsaHashtags*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto_2014/vectorInicial/*bolsaHashtags*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Volcano/vectorInicial/*bolsaHashtags*

echo "[ modelar ][ Delete Data ][ bolsaPalabras ]"
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorInicial/*bolsaPalabras*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorInicial/*bolsaPalabras*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorInicial/*bolsaPalabras*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto_2014/vectorInicial/*bolsaPalabras*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Volcano/vectorInicial/*bolsaPalabras*

echo "[ modelar ][ Delete Data ][ estructurales ]"
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorInicial/*estructurales*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorInicial/*estructurales*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorInicial/*estructurales*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto_2014/vectorInicial/*estructurales*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Volcano/vectorInicial/*estructurales*

echo "[ modelar ][ cd ../MineriaDeDatos/Mallet/Twitter/Data/ ]"
cd ../MineriaDeDatos/Mallet/Twitter/Data/

for i in */*/*.vector
do
	pathOutput=$(echo $i | awk '{split( $0, data, "/"); print data[1]"/"data[2]"/format/"}' )
	pathImport=$(echo $i | awk '{split( $0, data, "/"); print data[1]"/"data[2]"/import/"}')
	pathModel=$(echo $i | awk '{split( $0, data, "/"); print data[1]"/"data[2]"/model/"}')
	outputFile=$(echo $i | awk '{split( $0, data, "/"); print data[3]}')
	pathLog=$(echo $i | awk '{split( $0, data, "/"); print data[1]"/"data[2]"/log/"}')
	salida=$pathOutput$outputFile
	importFile=$pathImport$outputFile
	#modelos
	modelMaxEntFile=$pathModel$outputFile"MaxEnt.model"
	modelNaiveBayesFile=$pathModel$outputFile"NaiveBayes.model"
	modelWinnowFile=$pathModel$outputFile"Winnow.model"
	#logs 80
	logFileMaxEnt=$pathLog$outputFile"MaxEnt80.log"
	logFileNaiveBayes=$pathLog$outputFile"NaiveBayes80.log"
	logFileWinnow=$pathLog$outputFile"Winnow80.log"
	#logs 70
	logFileMaxEnt70=$pathLog$outputFile"MaxEnt70.log"
	logFileNaiveBayes70=$pathLog$outputFile"NaiveBayes70.log"
	logFileWinnow70=$pathLog$outputFile"Winnow70.log"

	cat $i | awk '{FS="\t"; print $2" "$3}' > $salida
	#Import Data
	echo "[ modelar ][ Mallet ][ Import Data ][ "$salida" ]"
	../../bin/mallet import-svmlight --input $salida --output $importFile

	#MaxEnt
	echo "[ modelar ][ Mallet ][ MaxEnt 80 ][ "$importFile" ]"
	../../bin/mallet train-classifier --input $importFile --trainer MaxEnt --training-portion 0.8 --report test:accuracy test:f1 --output-classifier $modelMaxEntFile > $logFileMaxEnt
	echo "[ modelar ][ Mallet ][ MaxEnt 70 ][ "$importFile" ]"
  ../../bin/mallet train-classifier --input $importFile --trainer MaxEnt --training-portion 0.7 --report test:accuracy test:f1 --output-classifier $modelMaxEntFile > $logFileMaxEnt70

	#NaiveBayes
	echo "[ modelar ][ Mallet ][ NaiveBayes 80 ][ "$importFile" ]"
	../../bin/mallet train-classifier --input $importFile --trainer NaiveBayes --training-portion 0.8 --report test:accuracy test:f1 --output-classifier $modelNaiveBayesFile > $logFileNaiveBayes
	echo "[ modelar ][ Mallet ][ NaiveBayes 70 ][ "$importFile" ]"
  ../../bin/mallet train-classifier --input $importFile --trainer NaiveBayes --training-portion 0.7 --report test:accuracy test:f1 --output-classifier $modelNaiveBayesFile > $logFileNaiveBayes70

	#Winnow
	echo "[ modelar ][ Mallet ][ Winnow 80 ][ "$importFile" ]"
	../../bin/mallet train-classifier --input $importFile --trainer Winnow  --training-portion 0.8 --report test:accuracy test:f1 --output-classifier $modelWinnowFile > $logFileWinnow
	echo "[ modelar ][ Mallet ][ Winnow 70 ][ "$importFile" ]"
  ../../bin/mallet train-classifier --input $importFile --trainer Winnow  --training-portion 0.7 --report test:accuracy test:f1 --output-classifier $modelWinnowFile > $logFileWinnow70

done

cd ../../../../Controlador/
echo "[ modelar ][ cd ../../../../Controlador/ ]"
now="$(date)"
echo "[ modelar ][ modelar.sh Terminado ][ "$now" ]"
