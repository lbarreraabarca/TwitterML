now="$(date)"
echo "[ convertSVMformat ][ convertSVMformat.sh Inicio ][ "$now" ]"

#Terremoto 2017 y Huracan
# for i in ../Data/*/*/*/*/vectorInicial/*
# do
  # echo "[ convertSVMformat ][ 1 ][ Ejecutando convertSVMformat ][ "$i" ]"
  # path_output=$(echo $i | awk '{split($0, data, "vectorInicial"); print data[1]}')
  # file=$(echo $i | awk '{split($0, data, "vectorInicial"); print data[2]}')
  # cat $i | awk '{FS="\t"; if($2=="CRI"){ print "1;"$3}else{ print "-1;"$3}}' > $path_output"vectorSVM"$file
  # echo "[ convertSVMformat ][ 1 ][ Ejecutando convertSVMformat Terminada ][ "$i" ]"
# done

#Nieve
# for j in ../Data/*/*/*/vectorInicial/*
# do
  # echo "[ convertSVMformat ][ 2 ][ Ejecutando convertSVMformat ][ "$j" ]"
  # path_output2=$(echo $j | awk '{split($0, data, "vectorInicial"); print data[1]}')
  # file=$(echo $j | awk '{split($0, data, "vectorInicial"); print data[2]}')
  # cat $j | awk '{FS="\t"; if($2=="CRI"){ print "1;"$3}else{ print "-1;"$3}}' > $path_output2"vectorSVM"$file
  # echo "[ convertSVMformat ][ 2 ][ Ejecutando convertSVMformat Terminada ][ "$j" ]"
# done

#Terremoto 2014
# for j in ../Data/*/*/vectorInicial/*
# do
  # echo "[ convertSVMformat ][ 3 ][ Ejecutando convertSVMformat ][ "$j" ]"
  # path_output2=$(echo $j | awk '{split($0, data, "vectorInicial"); print data[1]}')
  # file=$(echo $j | awk '{split($0, data, "vectorInicial"); print data[2]}')
  # cat $j | awk '{FS="\t"; if($2=="CRI"){ print "1;"$3}else{ print "-1;"$3}}' > $path_output2"vectorSVM"$file
  # echo "[ convertSVMformat ][ 3 ][ Ejecutando convertSVMformat Terminada ][ "$j" ]"
# done

#volcano 2014
for j in ../Data/EN/*/*/*/*/vectorInicial/*
do
  echo "[ convertSVMformat ][ 4 ][ Ejecutando convertSVMformat ][ "$j" ]"
  path_output2=$(echo $j | awk '{split($0, data, "vectorInicial"); print data[1]}')
  file=$(echo $j | awk '{split($0, data, "vectorInicial"); print data[2]}')
  cat $j | awk '{FS="\t"; if($2=="CRI"){ print "1;"$3}else{ print "-1;"$3}}' > $path_output2"vectorSVM"$file
  echo "[ convertSVMformat ][ 4 ][ Ejecutando convertSVMformat Terminada ][ "$j" ]"
done


now="$(date)"
echo "[ convertSVMformat ][ convertSVMformat.sh Terminado ][ "$now" ]"
