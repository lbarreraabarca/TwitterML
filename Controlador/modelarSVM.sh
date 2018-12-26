echo "[ modelarSVM ][ modelarSVM.sh Inicio ]"


#Huracanes
echo "[ modelarSVM ][ Copy Data ][ Huracan ][ Irma ]"
#cp -fr ../Data/Huracanes/2017/Sept/Irma/vectorSVM/train ../MineriaDeDatos/SVM-Light/Twitter/Data/Huracanes/
#cp -fr ../Data/Huracanes/2017/Sept/Irma/vectorSVM/test ../MineriaDeDatos/SVM-Light/Twitter/Data/Huracanes/

#Terremotos
echo "[ modelarSVM ][ Copy Data ][ Terremoto ][ 2017 ]"
cp -fr ../Data/Terremotos/2017/Sept/07/vectorSVM/train ../MineriaDeDatos/SVM-Light/Twitter/Data/Terremoto/
cp -fr ../Data/Terremotos/2017/Sept/07/vectorSVM/test ../MineriaDeDatos/SVM-Light/Twitter/Data/Terremoto/

#Nieve
echo "[ modelarSVM ][ Copy Data ][ Nieve ][ 2017 ]"
cp -fr ../Data/Nieve/2017/Jul/vectorSVM/train ../MineriaDeDatos/SVM-Light/Twitter/Data/Nieve/
cp -fr ../Data/Nieve/2017/Jul/vectorSVM/test ../MineriaDeDatos/SVM-Light/Twitter/Data/Nieve/

#Terremoto 2014
echo "[ modelarSVM ][ Copy Data ][ Terremoto ][ 2014 ]"
cp -fr ../Data/Terremotos/2014/vectorSVM/train ../MineriaDeDatos/SVM-Light/Twitter/Data/Terremoto_2014/
cp -fr ../Data/Terremotos/2014/vectorSVM/test ../MineriaDeDatos/SVM-Light/Twitter/Data/Terremoto_2014/

#Terremoto 2014
echo "[ modelarSVM ][ Copy Data ][ Volcano ][ 2014 ]"
Data/EN/Event/Volcano/2014/ago/
cp -fr ../Data/EN/Event/Volcano/2014/ago/vectorSVM/train ../MineriaDeDatos/SVM-Light/Twitter/Data/Volcano/
cp -fr ../Data/EN/Event/Volcano/2014/ago/vectorSVM/test ../MineriaDeDatos/SVM-Light/Twitter/Data/Volcano/

cd ../MineriaDeDatos/SVM-Light/Twitter/Data

for i in */train/*
do
	pathOutput=$(echo $i | awk '{split( $0, data, "train"); print data[1]}' )
	file=$(echo $i | awk '{split( $0, data, "train"); print data[2] }' )
	echo "[ modelarSVM ][ SVM-Light ][ Generar Modelo ][ "$i" ]"
	../../svm_learn $i $pathOutput"model/modelo.dat"
	echo "[ modelarSVM ][ SVM-Light ][ Clasificar ][ "$i" ]"
	../../svm_classify $pathOutput"test"$file"test" $pathOutput"model/modelo.dat" $pathOutput"salida.dat" > $pathOutput"result/result.dat"
	#echo $i
done

cd ../../../../Controlador
