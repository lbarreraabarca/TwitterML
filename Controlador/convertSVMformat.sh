
for i in ../Data/*/*/*/*/vectorInicial/*
do
  path_output=$(echo $i | awk '{split($0, data, "vectorInicial"); print data[1]}')
  file=$(echo $i | awk '{split($0, data, "vectorInicial"); print data[2]}')
  cat $i | awk '{FS="\t"; if($2=="CRI"){ print "1 "$3}else{ print "-1 "$3}}' > $path_output"vectorSVM"$file
done

for j in ../Data/*/*/*/vectorInicial/*
do
  path_output2=$(echo $j | awk '{split($0, data, "vectorInicial"); print data[1]}')
  file=$(echo $j | awk '{split($0, data, "vectorInicial"); print data[2]}')
  cat $j | awk '{FS="\t"; if($2=="CRI"){ print "1 "$3}else{ print "-1 "$3}}' > $path_output2"vectorSVM"$file
done
