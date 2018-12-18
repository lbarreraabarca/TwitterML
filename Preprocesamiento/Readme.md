## Pre procesamiento ##

Este componente está formado por los siguientes módulos.

- [EtiquetadoAutomatico](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/EtiquetadoAutomatico) se encarga de asignar una etiqueta a cada tuit. Esto se realiza a través del uso de [regresiones lineales](https://en.wikipedia.org/wiki/Linear_regression).
  Este proceso buscar estimar tendencias en ventanas de tiempo de 20 minutos. 
- [LimpiarDatos](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/LimpiarDatos) este proceso se encarga de transformar un tuit, de formato json a csv, obteniendo las siguientes columnas.
  
  Columna | Descripción
  ------------ | ------------- 
  ID Tuit | Identificador del tuit.
  Ts_minuto | Instante del tuit expresado en un Long.
  Hashtags | Listado de Hashtags.
  User Mentions | Listado de user mentions-
  Ubicación | Ubicación proporcionada por el usuario.
  RT count | Cantidad de retuits.
  Followers | Cantidad de seguidores.
  Text | Cuerpo del tuit. 
  Date | Fecha de publicación del tuit. 

- [UTIL](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/UTIL) se compone de un conjunto de algoritmos que permiten apoyar el proceso de análisis de los tuits. Estos son:
  - [NER_ES](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/UTIL/NER_ES) corresponde a un ejemplo ej python de como correr un NER en español.
  - [Verbos](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/UTIL/Verbos) Algoritmo que permite descargar verbos y sus conjugaciones.
  - [convertorFechas](https://github.com/lbarreraabarca/TwitterML/blob/master/Preprocesamiento/UTIL/convertorFechas) transforma una fecha TIMESTAMP del tipo *Fri Sep 08 10:04:11 +0000 2017* en un Long. 
  - [eliminarRepetidos](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/UTIL/eliminarRepetidos) elimina los tuits con ID iguales de un conjunto de datos. 	
  - [husosHorarios](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/UTIL/husosHorarios)	csv que almacena los husos horarios de distintos países. 
  - [pruebaSerie](https://github.com/lbarreraabarca/TwitterML/blob/master/Preprocesamiento/UTIL/pruebaSerie) ejercicio de prueba para calculo de coeficiente de variación por serie. 
  - [visualizarSeries](https://github.com/lbarreraabarca/TwitterML/blob/master/Preprocesamiento/UTIL/visualizarSeries) permite visualizar el valor de la regresión lineal por ventana de tiempo. 
