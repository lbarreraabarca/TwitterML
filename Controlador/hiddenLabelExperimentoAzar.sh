

cat ../Data/Nieve/2017/Jul/label/nieve..csv | awk '{FS="\t"; print $1"\t"$11}' > tmp.txt
while read line 
do
	idTuit=$(echo $line | awk '{FS="\t"; print $1 }')
	a=$(grep $idTuit tmp.txt | awk '{FS=" "; print $2}')
	echo $a
done < huracan
