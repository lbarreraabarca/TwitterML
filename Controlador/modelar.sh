
echo "Copiando..."
#Huracanes
cp -fr ../Data/Huracanes/2017/Sept/Irma/vectorInicial/* ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorInicial/
#cp -fr ../Data/Huracanes/2017/Sept/Irma/vectorFinal/* ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorFinal/
#Terremotos
cp -fr ../Data/Terremotos/2017/Sept/07/vectorInicial/* ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorInicial/
#cp -fr ../Data/Terremotos/2017/Sept/07/vectorFinal/* ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorFinal/ 
#Nieve
cp -fr ../Data/Nieve/2017/Jul/vectorInicial/* ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorInicial/
#cp -fr ../Data/Nieve/2017/Jul/vectorFinal/* ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorFinal/ 

echo "Eliminando..."
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorInicial/*bolsaUserMentions*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorInicial/*bolsaUserMentions*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorInicial/*bolsaUserMentions*

rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Huracanes/vectorInicial/*bolsaHashtags*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Terremoto/vectorInicial/*bolsaHashtags*
rm -fr ../MineriaDeDatos/Mallet/Twitter/Data/Nieve/vectorInicial/*bolsaHashtags*


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
	modelMaxEntFile=$pathModel$outputFile"MaxEnt.model"
	modelNaiveBayesFile=$pathModel$outputFile"NaiveBayes.model"
	logFileMaxEnt=$pathLog$outputFile"MaxEnt80.log"
	logFileNaiveBayes=$pathLog$outputFile"NaiveBayes80.log"
	logFileMaxEnt70=$pathLog$outputFile"MaxEnt70.log"
	logFileNaiveBayes70=$pathLog$outputFile"NaiveBayes70.log"
	cat $i | awk '{FS="\t"; print $2" "$3}' > $salida
	#cat $salida
	echo "Realizando Import... "$salida
	../../bin/mallet import-svmlight --input $salida --output $importFile
	echo "Generando Modelo MaxEnt 80..."
	../../bin/mallet train-classifier --input $importFile --trainer MaxEnt --training-portion 0.8 --report test:accuracy test:f1 --output-classifier $modelMaxEntFile > $logFileMaxEnt
	echo "Generando Modelo MaxEnt 70..."
  ../../bin/mallet train-classifier --input $importFile --trainer MaxEnt --training-portion 0.7 --report test:accuracy test:f1 --output-classifier $modelMaxEntFile > $logFileMaxEnt70

	echo "Generando Modelo NaiveBayes..."
	../../bin/mallet train-classifier --input $importFile --trainer NaiveBayes --training-portion 0.8 --report test:accuracy test:f1 --output-classifier $modelNaiveBayesFile > $logFileNaiveBayes

	echo "Generando Modelo NaiveBayes 70..."
  ../../bin/mallet train-classifier --input $importFile --trainer NaiveBayes --training-portion 0.7 --report test:accuracy test:f1 --output-classifier $modelNaiveBayesFile > $logFileNaiveBayes70
	echo "-------------------------------"
done 
