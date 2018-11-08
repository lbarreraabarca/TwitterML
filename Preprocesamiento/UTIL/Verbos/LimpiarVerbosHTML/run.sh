
make clean
make 


for i in ../../../../Data/Palabras/Verbos/HTML/*
do
	make run IN=$i OUT=../../../../Data/Palabras/Verbos/Conjugados/ 
done
