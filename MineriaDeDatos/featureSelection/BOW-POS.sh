for i in input/BOW-POS/* 
do
	folderOUT=$( echo $i | awk '{ split( $0, data, "/"); print data[ 3 ]}')
	Filevector=$(ls $i/*vector)
	FileBOW=$(ls $i/*dat)
	echo "Procesando "$folderOUT
	if [ -d output/BOW-POS/$folderOUT ];
	then
		rm -fr output/BOW-POS/$folderOUT
	fi
	mkdir output/BOW-POS/$folderOUT
	python bow-SFS.py $Filevector $FileBOW input/classes/class.dat output/BOW-POS/$folderOUT/$folderOUT.vector output/BOW-POS/$folderOUT/$folderOUT.sfs output/BOW-POS/$folderOUT/metrics-$folderOUT.dat
	#python bow-SFS.py input/$i/stopwords-$i.dat input/$i/bolsaStopwords-$i.bow input/classes/class.dat output/$i/stopwords-$i.dat
done

