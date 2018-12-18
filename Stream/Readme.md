## Stream ##
Este módulo está compuesto por los siguientes componentes:

- [DN-Twitter](https://github.com/lbarreraabarca/TwitterML/tree/master/Stream/DN-Twitter) este módulo se encarga de administrar la disponibilidad de proxies para realizar la descarga de tuits, de forma paralela en un mismo nodo de procesamiento.
- [StreamingAPI](https://github.com/lbarreraabarca/TwitterML/tree/master/Stream/StreamingAPI) este módulo corresponde al proceso de descarga de tuits. Estos son almacenados en la base de datos MongoDB. 
- [StreamingClassifier](https://github.com/lbarreraabarca/TwitterML/tree/master/Stream/StreamingClassifier) este módulo se encarga de leer los tuits almacenados, en la base de datos MongoDB, y le asigna una etiqueta a cada tuit. Posteriormente actualiza el tuit en la base de datos con la etiqueta asignada. 
- [UTIL](https://github.com/lbarreraabarca/TwitterML/tree/master/Stream/UTIL) este módulo corresponde a algoritmos de pruebas. 
