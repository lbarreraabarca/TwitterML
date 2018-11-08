import csv
import sys
import numpy as np
from sklearn.neighbors import KNeighborsClassifier
from mlxtend.feature_selection import SequentialFeatureSelector as SFS

def buscarElemento(lista, elemento):
    for i in range(0,len(lista)):
        if(lista[i] == elemento):
            return i
#Inicio Codigo
if ( len( sys.argv ) - 1 )!= 7:
    print 'Error en la cantidad de argumentos.'
    sys.exit(ExitStatus.success)

#Lectura de Archivos
bowFile   = open( sys.argv[ 2 ], "r" )    # Lectura de archivos con el conjunto de caracteristicas.
dataFile  = open( sys.argv[ 1 ], "r" )    # Lectura de las instancias ( tuits ) con sus respectivas caracteristicas modeladas.
labelFile = open( sys.argv[ 3 ], "r" )    # Lectura de archivo con el listado de CLASES.
azarFile  = open( sys.argv[ 7 ], "r" )    # Lectura de archivo de instancias al azar.

BOW=[]
CLASS=[]
AZAR=[]

print 'Cargando listado de clases'
for i in labelFile:
    i=i[:-1]
    CLASS.append( i )

print 'Cargando Caracteristicas'
for i in bowFile:
    BOW.append( i )

print 'Cargando instancias Azar'
for i in azarFile:
    i=i[:-1]
    AZAR.append( i )

matrix = []
vlabel = []

profile=[]
print 'Cargando Vectores de Caracteristicas'
for instance in dataFile:
    data = instance.split( '\t' )
    if len( data ) == 3:
        features =  data[ 2 ].split( ' ' )
        label    =  data[ 1 ]
        idTuit   =  data[ 0 ]
        if idTuit in AZAR:
            vector = [0] * len( BOW )
            for i in features:
                feat = i.split( ':' )
                if len(feat) == 2:
                    position = np.int64( feat[ 0 ] )
                    position = position - 1
                    weight = np.float64( feat[ 1 ] )
                    vector[ position ] = weight
            numLabel = buscarElemento(CLASS, label)
            matrix.append( vector )
            vlabel.append( numLabel )
            profile.append( idTuit )

bowFile.close( )    # Se cierra el archivo que contiene el conjunto de caracteristicas
dataFile.close()    # Se cierra el archivo que contiene las instancias
labelFile.close()   # Se cierra el archivo que contiene el listado de CLASES.

X = np.array( matrix ) 
print 'Ejecucion del proceso de seleccion de caracteristicas'
knn = KNeighborsClassifier(n_neighbors=3 )  # Se aplica algoritmo de vecinos mas cercanos para la seleccion de caracteristicas

sfs1 = SFS(knn,
           k_features=( 1, len( BOW ) ), 
           forward=True, 
           floating=False, 
           verbose=2,
           scoring='accuracy', #'f1_weighted',#'recall_macro',
           n_jobs=-1,#1,
           cv=5)

sfs1 = sfs1.fit( X , vlabel )   # Se aplica el algoritmo SFS.

print 'Escribiendo listado de caracteristicas seleccionadas'
#Archivos de salida
outputFile = open( sys.argv[ 4 ], "w" )     # Archivo de escritura de las instancias con sus respectivas caracteristicas seleccionadas.
outputFSFS = open( sys.argv[ 5 ], "w" )     # Listado de las caracteristicas seleccionadas.
scoreFile = open( sys.argv[ 6 ], "w" )      # Listado con valores de las metricas obtenidas.

featureSelected = sfs1.k_feature_idx_
for feati in featureSelected:
    outputFSFS.write( BOW[ feati ] )

posLabel=0
for instance in X:
    numLabel = np.int64( vlabel[ posLabel ] )
    salida = profile[ posLabel ] + "\t" + CLASS[ numLabel ]  + "\t";
    for feati in featureSelected:
        posFeat=0
        for features in np.nditer( instance ):
            if feati == posFeat:
                weigth = np.float64( features )
                ps = feati + 1
                if weigth > 0:
                    salida = salida + " " + str(ps) + ":" + str(weigth)
            posFeat = posFeat + 1
    posLabel = posLabel + 1
    outputFile.write( salida + "\n" )

scoreFile.write( "F1-Score : " + str( sfs1.k_score_ )  + "\n" )
#TODO: Cerrar los archivos.
outputFile.close()  # Se cierra el archivo que imprime las instancias
outputFSFS.close()  # Se cierra el archivo que imprime las caracteristicas seleccionadas
scoreFile.close()   # Se cierra el archivo que imprime la metrica F1-Score.


