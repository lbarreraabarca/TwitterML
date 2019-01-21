now="$(date)"
echo "[ main ][ main.sh Inicio ][" $1 "][ "$now" ]"

./preprocesar.sh			#Preprocesamiento Transformar de JSON a CSV
./eliminarRepetidos.sh		#Eliminar si existen tuits repetidos
./etiquetadoAutomatico.sh	#Proceso de etiquetas automaticas
./vectorizar.sh $1			#Tranformar a vector el csv
./convertSVMformat.sh		#Transformar a formato SVM-Light el vector
./divideTrainTestSVM.sh 	#dividir en porcentajes el Formnato SVM
###./obtenerInstanciasAzar.sh
###./PCA.sh
./modelar.sh 				#Modelar MALLET. Modelos MaxEnt, Bayes y Winnow
./modelarSVM.sh 			#Modelar SVM. 

./getResultMALLET.sh $1 	#Recopilar los resultos de MALLET. Modeos MaxEnt, BAyes y Winnow
./getResultSVM.sh $1 		#Recopilar resultados de SVM. 


now="$(date)"
echo "[ main ][ main.sh Terminado ][" $1 "][ "$now" ]"
