for i in input/BOW-NER/* 
do
	folderOUT=$( echo $i | awk '{ split( $0, data, "/"); print data[ 3 ]}')
	Filevector=$(ls $i/*vector)
	FileBOW=$(ls $i/*dat)
	echo "Procesando "$folderOUT
	if [ -d output/BOW-NER/$folderOUT ];
	then
		rm -fr output/BOW-NER/$folderOUT
	fi
	mkdir output/BOW-NER/$folderOUT
	python bow-SFS.py $Filevector $FileBOW input/classes/class.dat output/BOW-NER/$folderOUT/$folderOUT.vector output/BOW-NER/$folderOUT/$folderOUT.sfs output/BOW-NER/$folderOUT/metrics.dat

done

