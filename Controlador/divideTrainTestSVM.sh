now="$(date)"
echo "[ divideTrainTestSVM ][ divideTrainTestSVM.sh Inicio ][ "$now" ]"

echo "[ divideTrainTestSVM ][ cd ../MineriaDeDatos/UTIL/divideTrainTest/ ]"
cd ../MineriaDeDatos/UTIL/divideTrainTest/
echo "[ divideTrainTestSVM ][ Compilacion make ]"

make clean
make

# Terremoto 2017 y Huracan
# for input in ../../../Data/*/*/*/*/vectorSVM/*bolsaPalabras.vector
# do
  # echo "[ divideTrainTestSVM ][ 1 ][ Ejecutando divideTrainTestSVM ][ "$input" ]"
  # path_output=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[1]}')
  # output_file=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[2]}')
#
  # train=$path_output"vectorSVM/train"$output_file".70.train"
  # test=$path_output"vectorSVM/test"$output_file".70.test"
	# make run IN=$input OUTPUT_FILE_TRAIN=$train OUTPUT_FILE_TEST=$test TRAIN_PERCENT="0.7"
  # echo "[ divideTrainTestSVM ][ 1 ][ Ejecutando divideTrainTestSVM Terminada ][ "$input" ]"
# done

#Nieve
# for input in ../../../Data/*/*/*/vectorSVM/*bolsaPalabras.vector
# do
	# echo "[ divideTrainTestSVM ][ 2 ][ Ejecutando divideTrainTestSVM ][ "$input" ]"
  # path_output=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[1]}')
  # output_file=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[2]}')
#
  # train=$path_output"vectorSVM/train"$output_file".70.train"
  # test=$path_output"vectorSVM/test"$output_file".70.test"
	# make run IN=$input OUTPUT_FILE_TRAIN=$train OUTPUT_FILE_TEST=$test TRAIN_PERCENT="0.7"
  # echo "[ divideTrainTestSVM ][ 2 ][ Ejecutando divideTrainTestSVM Terminada ][ "$input" ]"
# done

# Terremoto 2014
# for input in ../../../Data/*/*/vectorSVM/*bolsaPalabras.vector
# do
	# echo "[ divideTrainTestSVM ][ 3 ][ Ejecutando divideTrainTestSVM ][ "$input" ]"
  # path_output=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[1]}')
  # output_file=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[2]}')
#
  # train=$path_output"vectorSVM/train"$output_file".70.train"
  # test=$path_output"vectorSVM/test"$output_file".70.test"
	# make run IN=$input OUTPUT_FILE_TRAIN=$train OUTPUT_FILE_TEST=$test TRAIN_PERCENT="0.7"
  # echo "[ divideTrainTestSVM ][ 3 ][ Ejecutando divideTrainTestSVM Terminada ][ "$input" ]"
# done

# Volcano 2014
for input in ../../../Data/EN/*/*/*/*/vectorSVM/*bolsaPalabras.vector
do
	echo "[ divideTrainTestSVM ][ 4 ][ Ejecutando divideTrainTestSVM ][ "$input" ]"
  path_output=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[2]}')

  train=$path_output"vectorSVM/train"$output_file".70.train"
  test=$path_output"vectorSVM/test"$output_file".70.test"
	make run IN=$input OUTPUT_FILE_TRAIN=$train OUTPUT_FILE_TEST=$test TRAIN_PERCENT="0.7"
  echo "[ divideTrainTestSVM ][ 4 ][ Ejecutando divideTrainTestSVM Terminada ][ "$input" ]"
done


now="$(date)"
cd ../../../Controlador/
echo "[ divideTrainTestSVM ][ cd ../../../Controlador/ ]"
echo "[ divideTrainTestSVM ][ divideTrainTestSVM.sh Terminada ][ "$now" ]"
