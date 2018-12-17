# [ Twitter Burst Detection ]

Este repositorio corresponde a la tesis de grado de Luis Barrera Abarca, estudiante de la Universidad Andrés Bello. La tesis se titula "Detección de tópicos ráfaga en Twitter".

## Prerequisitos ##
Se debe contar con una máquina Linux, sobre ella instalar los siguientes componentes de softwares:

  - [Java 8 o superior](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - [NodeJS](https://nodejs.org/es/download/package-manager/)
  - [MongoDB](https://docs.mongodb.com/manual/installation/)
  - [Python 2.7 o superior](https://docs.aws.amazon.com/cli/latest/userguide/install-linux-python.html)
  
 ## Descargar repositorio ##
 ``` Bash
 $ git clone https://github.com/lbarreraabarca/TwitterML.git
 $ cd TwitterML/
 
```
 ## Descargar datos ##
 Los datos se debem descargar de la siguiente fuente [Data.tar.gz](https://drive.google.com/open?id=1EGA9rgxoh-tAEjNWodRetw-Jjvw5rwEL). Este conjunto de datos se compone de cuatro conjuntos de datos. Estos son los siguientes:
 
  Evento | Lugar | Fecha
  ------------ | ------------- | -------------
  [Terremoto](https://es.wikipedia.org/wiki/Terremoto_de_Iquique_de_2014) | Iquique, Chile | 01 Abr 2014
  [Terremoto](https://es.wikipedia.org/wiki/Terremoto_de_Chiapas_de_2017) | Chiapas, México | 07 Sep 2017
  [Huracán Irma](https://es.wikipedia.org/wiki/Hurac%C3%A1n_Irma) | Mar del Caribe | Sep 2017
  [Nieve](https://www.24horas.cl/nacional/nevazon-en-santiago-seria-la-mas-grande-en-santiago-en-45-anos-2448957) | Stgo, Chile | Jul 2017
  
  
  Una vez descargado el conjunto de datos [Data.tar.gz](https://drive.google.com/open?id=1EGA9rgxoh-tAEjNWodRetw-Jjvw5rwEL), se deben realizar los siguientes pasos:
  
``` Bash
 $ tar xzvf Data.tar.gz
 $ mv Data/ ./
 
```
  
### Integrar Mallet ###
Para integrar la herramienta Mallet se debe descargar desde el siguiente link [Mallet](https://drive.google.com/open?id=1uxUauF1uzJxcVwEOSaU3WHYRxGKTScj1).

``` Bash
 $ tar xzvf Mallet.tar.gz
 $ mv Mallet/ ./MineriaDeDatos/
 
```

### Integrar SVM-Light ###
Para integrar la herramienta SVM-Light se debe descargar desde el siguiente link [SVM-Light](https://drive.google.com/open?id=1gWSWjt9FcIcuAA-eyMm17Ydnd1NYQftV).

``` Bash
 $ tar xzvf SVM-Light.tar.gz
 $ mv SVM-Light/ ./MineriaDeDatos/
 
```
