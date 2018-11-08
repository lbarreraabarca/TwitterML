JC=javac
CLASSPATH=".:./lib/jsoup-1.8.3.jar"

all: GetProxyList.class

GetProxyList.class: GetProxyList.java
	$(JC) -classpath $(CLASSPATH) GetProxyList.java

clean:
	rm -fr *.class

run:
	java -cp $(CLASSPATH) GetProxyList $(IN) $(OUT)
