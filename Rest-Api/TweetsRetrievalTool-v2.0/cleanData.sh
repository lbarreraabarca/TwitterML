
for i in Data/*/row/*
do
    echo "Procesando : "$i" ..."
    output=$( echo $i | awk '{split($0, data, "row" ); print data[1];}' )
    file=$( echo $i | awk '{split($0, data, "row" ); print data[2];}' )
    #echo $output"preprocesada"$file;

    cat $i | awk 'BEGIN{cont=1;}{split($0, data, ","); if( cont>1){print substr(data[1], 2, length(data[1])-2);} cont=cont+1;}' > $output"preprocesada"$file
    echo "Copiando : tweet_ids.txt"
    cp $output"preprocesada"$file ./tweet_ids.txt
    echo "Descargando : "$file
    java -classpath TweetsRetrievalTool-2.0.jar qa.org.qcri.tweetsretrievaltool.TweetsRetrievalTool tweet_ids.txt output.txt
    cp tweet2.json $output"json"$file
done
