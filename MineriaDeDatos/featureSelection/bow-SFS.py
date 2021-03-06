import csv
import sys
import numpy as np
from sklearn.neighbors import KNeighborsClassifier
from mlxtend.feature_selection import SequentialFeatureSelector as SFS
#from sklearn.linear_model import LinearRegression

def buscarElemento(lista, elemento):
    for i in range(0,len(lista)):
        if(lista[i] == elemento):
            return i
#INIT
bowFile = open( sys.argv[ 2 ], "r" )
dataFile = open( sys.argv[ 1 ], "r" )
labelFile = open( sys.argv[ 3 ], "r" )

BOW=[]
CLASS=[]

print 'Cargando clases'
for i in labelFile:
    i=i[:-1]
    CLASS.append( i )

print 'Cargando features'
for i in bowFile:
    BOW.append( i )

print len(BOW)
matrix = []
vlabel = []

profile=[]
print 'Cargando Vectores de Caracteristicas...'
for instance in dataFile:
    data = instance.split( '\t' )
    features =  data[ 2 ].split( ' ' )
    vector = [0] * len( BOW )
    for i in features:
        feat = i.split( ':' )
        if len(feat) == 2:
            position = np.int64( feat[ 0 ] )
            position = position - 1
            weight = np.float64( feat[ 1 ] )
            #print 'position : ', position, ' len( BOW ) : ',len( BOW ) 
            vector[ position ] = weight
    label = data[ 1 ]
    numLabel = buscarElemento(CLASS, label)
    matrix.append( vector )
    vlabel.append( numLabel )
    profile.append( data[ 0 ] )

X = np.array( matrix )
#algoritmo de seleccion de caracteristicas
print 'Comienza el proceso de seleccion...'
knn = KNeighborsClassifier(n_neighbors=3 )
#lr = LinearRegression()

sfs1 = SFS(knn,
           k_features=( 1, len( BOW ) ), 
           forward=True, 
           floating=False, 
           verbose=2,
           scoring='f1_macro', #'f1_weighted',#'recall_macro',
           n_jobs=1,#-1,
           cv=5)

sfs1 = sfs1.fit( X , vlabel)

print 'Obteniendo caracteristicas seleccionadas'
#OUTPUT FILE
outputFile = open( sys.argv[ 4 ], "w" )
outputFSFS = open( sys.argv[ 5 ], "w" )
scoreFile = open( sys.argv[ 6 ], "w" )

featureSelected = sfs1.k_feature_idx_
for feati in featureSelected:
    print feati
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
