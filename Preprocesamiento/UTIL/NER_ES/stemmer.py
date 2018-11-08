from nltk.stem.snowball import SnowballStemmer
stemmer = SnowballStemmer("spanish")

print(stemmer.stem("buscando"))
print(stemmer.stem("buscare"))
print(stemmer.stem("buscaste"))
print(stemmer.stem("buscamos"))
print(stemmer.stem("buscar"))
print(stemmer.stem("busque"))

print(stemmer.stem("arboles"))
print(stemmer.stem("arbol"))
print(stemmer.stem("arboleda"))


