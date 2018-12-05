
echo "Copiando..."
#Huracanes
#cp -fr ../Data/Huracanes/2017/Sept/Irma/vectorSVM/train ../MineriaDeDatos/SVM-Light/Twitter/Data/Huracanes/
#cp -fr ../Data/Huracanes/2017/Sept/Irma/vectorSVM/test ../MineriaDeDatos/SVM-Light/Twitter/Data/Huracanes/
#Terremotos
cp -fr ../Data/Terremotos/2017/Sept/07/vectorSVM/train ../MineriaDeDatos/SVM-Light/Twitter/Data/Terremoto/
cp -fr ../Data/Terremotos/2017/Sept/07/vectorSVM/test ../MineriaDeDatos/SVM-Light/Twitter/Data/Terremoto/
#Nieve
cp -fr ../Data/Nieve/2017/Jul/vectorSVM/train ../MineriaDeDatos/SVM-Light/Twitter/Data/Nieve/
cp -fr ../Data/Nieve/2017/Jul/vectorSVM/test ../MineriaDeDatos/SVM-Light/Twitter/Data/Nieve/

cd ../MineriaDeDatos/SVM-Light/Twitter/Data

for i in */train/*
do
	pathOutput=$(echo $i | awk '{split( $0, data, "train"); print data[1]}' )
	file=$(echo $i | awk '{split( $0, data, "train"); print data[2] }' )
	../../svm_learn $i $pathOutput"model/modelo.dat"
	../../svm_classify $pathOutput"test"$file"test" $pathOutput"model/modelo.dat" $pathOutput"salida.dat" > $pathOutput"result/result.dat"
	#echo $i
done

cd ../../../../Controlador
