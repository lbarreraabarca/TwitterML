make clean
make

rm ../../../Data/Nieve/2017/Jul/frecuenciaSerie/analisis-hora/frecuencia/serieNieve*
rm ../../../Data/Nieve/2017/Jul/frecuenciaSerie/analisis-hora/modelo/serieNieve*

make run in=../../../Data/Nieve/2017/Jul/csv/nieve.csv word=../../../Data/Nieve/2017/Jul/keywords/keywords.dat output=../../../Data/Nieve/2017/Jul/frecuenciaSerie/analisis-hora/frecuencia/serieNieve models=../../../Data/Nieve/2017/Jul/frecuenciaSerie/analisis-hora/modelo/serieNieve

rm ../../../Data/Terremotos/2017/Sept/07/frecuenciaSerie/analisis-hora/frecuencia/serieTerremoto*
rm ../../../Data/Terremotos/2017/Sept/07/frecuenciaSerie/analisis-hora/modelo/serieTerremoto*

make run in=../../../Data/Terremotos/2017/Sept/07/csv/terremoto-mexico1.csv word=../../../Data/Terremotos/2017/Sept/07/keywords/keywords.dat output=../../../Data/Terremotos/2017/Sept/07/frecuenciaSerie/analisis-hora/frecuencia/serieTerremoto models=../../../Data/Terremotos/2017/Sept/07/frecuenciaSerie/analisis-hora/modelo/serieTerremoto

rm ../../../Data/Huracanes/2017/Sept/Irma/frecuenciaSerie/analisis-hora/frecuencia/serieHuracan*
rm ../../../Data/Huracanes/2017/Sept/Irma/frecuenciaSerie/analisis-hora/modelo/serieHuracan*

make run in=../../../Data/Huracanes/2017/Sept/Irma/csv/huracan-irma.csv word=../../../Data/Huracanes/2017/Sept/Irma/keywords/keywords.word output=../../../Data/Huracanes/2017/Sept/Irma/frecuenciaSerie/analisis-hora/frecuencia/serieHuracan models=../../../Data/Huracanes/2017/Sept/Irma/frecuenciaSerie/analisis-hora/modelo/serieHuracan
