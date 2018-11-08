#!/bin/bash
CLASSPATH=".:lib/twitter4j/twitter4j-async-4.0.4.jar:lib/twitter4j/twitter4j-core-4.0.4.jar:lib/twitter4j/twitter4j-examples-4.0.4.jar:lib/twitter4j/twitter4j-media-support-4.0.4.jar:lib/twitter4j/twitter4j-stream-4.0.4.jar:lib/json/json-simple-1.1.1.jar:lib/mail/mail-1.4.7.jar"
  rm logs/* 
  make clean
  make
  cp ../DN-Twitter/controller/proxy* input/
  #cat input/proxy* > input/proxies.dat
  #mv input/proxies.dat input/proxy0.dat
  day=`date +"%d-%m-%Y"`
  hr=`date +"%H-%M"`
  fileTerms=input/terminos.dat
  fileCta=input/cuentas.dat
  stopw=input/stopwords_es.dat
  largoWindow=3
  cv=1.2
  constantSTD=2
  currentFrecuency=50
  mapTerms=input/mapterminos.dat
  while read line;
  do
    numTerm=$(echo $line | awk '{FS=" "; print $1}')
    term=$(echo $line | awk '{FS=" "; print $2}')
    echo -e "Ejecutando termino : "$term"\t"$numTerm
    #PARAMETROS
    numCta=$numTerm
    proxyFile=input/proxy$numTerm.dat
    outputTuits=output/$term"-"$hr"-"$day
    tuitsForDay=records/$term"-"$hr"-"$day
    logData=logs/$term"-"$hr"-"$day
		java -cp $CLASSPATH main $numCta $numTerm $outputTuits $proxyFile $fileTerms $fileCta $stopw $largoWindow $cv $constantSTD $currentFrecuency $tuitsForDay $mapTerms $term > $logData & 
  done < $mapTerms
