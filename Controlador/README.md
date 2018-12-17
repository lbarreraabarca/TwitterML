# [ Módulo Controlador ]

Este módulo corresponde a la capa donde se ejecutan todos los procesos asociados al Machine Learning. 

## Main ##
El código [main.sh](https://github.com/lbarreraabarca/TwitterML/blob/master/Controlador/main.sh) se encarga de ejecutar cada uno de los procesos asociados
a procesamiento de Machine Learning.

### Pre procesamiento ###
El script [preprocesar.sh](https://github.com/lbarreraabarca/TwitterML/blob/master/Controlador/preprocesar.sh) ejecuta el módulo [LimpiarDatos](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/LimpiarDatos)
 que se encarga de extraer la información del tuit y almacenarla en un csv.

### Eliminar Repetidos ###
El script [eliminarRepetidos.sh](https://github.com/lbarreraabarca/TwitterML/blob/master/Controlador/eliminarRepetidos.sh) ejecuta el módulo [eliminarRepetidos](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/UTIL/eliminarRepetidos)
 que se encarga de eliminar del conjunto de datos los tuits que se encuentren repetidos. Para determinar si un tuit está repetido se compara a través de su id. 

### Etiquetado automático ###
El script [etiquetadoAutomatico.sh](https://github.com/lbarreraabarca/TwitterML/blob/master/Controlador/etiquetadoAutomatico.sh) ejecuta el módulo [EtiquetadoAutomatico](https://github.com/lbarreraabarca/TwitterML/tree/master/Preprocesamiento/EtiquetadoAutomatico)
que se encarga de asignar una etiqueta a cada tuit. Este proceso se realizar a través del cálculo de regresiones lineales. 

  
  

