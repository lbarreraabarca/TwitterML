JC=javac
CLASSPATH=".:./jsoup-1.8.3.jar"

all: GetProxyList.class

GetProxyList.class: GetProxyList.java
	$(JC) -classpath $(CLASSPATH) GetProxyList.java

clean:
	rm -fr *.class

run:
	rm -f proxies.dat tmp.dat
	java -cp $(CLASSPATH) GetProxyList
	sort -u proxies.dat > tmp.dat
	cat tmp.dat | awk '{if(NF==2) print $0;}' > tmp2.dat
	mv tmp2.dat proxies.dat

