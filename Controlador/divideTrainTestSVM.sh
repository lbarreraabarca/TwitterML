
cd ../MineriaDeDatos/UTIL/divideTrainTest/
echo "***************************************************************************************"
echo "Compilando Divide Traint Test..."
make clean
make
echo "***************************************************************************************"

for input in ../../../Data/*/*/*/*/vectorSVM/*bolsaPalabras.vector
do
  echo "Ejecutando Divide Train Test : "$input
  path_output=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[2]}')

  train=$path_output"vectorSVM/train"$output_file".70.train"
  test=$path_output"vectorSVM/test"$output_file".70.test"
	make run IN=$input OUTPUT_FILE_TRAIN=$train OUTPUT_FILE_TEST=$test TRAIN_PERCENT="0.7"

done

for input in ../../../Data/*/*/*/vectorSVM/*bolsaPalabras.vector
do
	echo "Ejecutando Divide Train Test : "$input
  path_output=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[1]}')
  output_file=$(echo $input | awk '{split($0, data, "vectorSVM"); print data[2]}')

  train=$path_output"vectorSVM/train"$output_file".70.train"
  test=$path_output"vectorSVM/test"$output_file".70.test"
	make run IN=$input OUTPUT_FILE_TRAIN=$train OUTPUT_FILE_TEST=$test TRAIN_PERCENT="0.7"

  echo "********************************************************************************************************"
done

cd ../../../Controlador/
