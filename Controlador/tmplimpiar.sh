x="caldera";

rm salida.dat

while IFS= read -r var
do
  #echo $var
  x=$(echo $var)

  min=$(cat pruebaMinMax.dat | grep -w $x | sort -k2 -n  | uniq -c | head -1 | awk '{print $3}')
  max=$(cat pruebaMinMax.dat | grep -w $x | sort -k2 -n  | uniq -c |tail -1 | awk '{print $3}')
  echo -e $x"\t"$min"\t"$max >> salida.dat
done < entradaTMp.txt
