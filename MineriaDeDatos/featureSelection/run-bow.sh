for i in {v..v}
do
	echo "Testeando carpeta... "$i
	if [ -d output/$i ];
	then
		rm -fr output/$i
	fi
	#mkdir output/BOW-POS/$i
	#python bow-SFS.py input/$i/outLema-$i.dat input/$i/bolsaLemma-$i.bow input/classes/class.dat output/$i/lemma-$i.dat output/$i/SFS-$i.dat
	#python bow-SFS.py input/$i/stopwords-$i.dat input/$i/bolsaStopwords-$i.bow input/classes/class.dat output/$i/stopwords-$i.dat
done

