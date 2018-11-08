from sklearn.neighbors import KNeighborsClassifier
from mlxtend.feature_selection import SequentialFeatureSelector as SFS
from sklearn.datasets import load_iris
import numpy as np
#import sys

iris = load_iris()
X = iris.data
y = iris.target

print y
knn = KNeighborsClassifier(n_neighbors=4)

sfs1 = SFS(knn,
           k_features=4, 
           forward=True, 
           floating=False, 
           verbose=2,
           scoring='recall_macro',
           cv=5)
print 'comienza a seleccionar....'
sfs1 = sfs1.fit(X, y)

print('Selected features:', sfs1.k_feature_idx_)
#print 'Caracteristicas Seleccionadas'
#abd = open( sys.argv[ 1 ], 'w' )
#a = sfs1.k_feature_idx_
#for i in a:
#    abd.write( str( i ) )
#print sfs1.k_feature_idx_
