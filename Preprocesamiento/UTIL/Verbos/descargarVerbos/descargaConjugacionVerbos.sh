
url="conjugador.reverso.net/conjugacion-espanol-verbo-"
while read line
do
	echo $url$line".html"
	wget $line".html" -O ../../../../Data/Palabras/Verbos/HTML/$line".html" $url$line".html"
done < listadoUnico.dat


# ../../../../Data/Palabras/Verbos/HTML/

#wget <file.ext> -O /path/to/folder/file.ext
