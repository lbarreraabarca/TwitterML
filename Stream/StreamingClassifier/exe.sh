
while read line
do
	echo $line
	instancias="input/"$line"/instancias.dat"
	bolsaP="../../Data/Terremotos/2017/Sept/07/bolsa/terremoto-mexico1-bolsaPalabras.bow"
	vectorizado="output/"$line"/vectorizado-instancias.dat"	
	clasificado="output/"$line"/clasificado-instancias.dat"
	model="models/"$line"/naiveBayes/model"
	make clean
	make
	make run INPUT_INSTANCIAS=$instancias BOLSA=$bolsaP OUTPUT_VECTORIZADO=$vectorizado CONJUNTO_DATO=$line VERBOS="../../Data/Palabras/Verbos/ConjugadosUnion/listadoverbos.verbo" TIME_WINDOW=5 OUTPUT_CLASIFICADO=$clasificado INPUT_MODEL=$model

done < input/test-terminos.dat
