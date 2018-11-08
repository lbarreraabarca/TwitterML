JC=javac
CLASSPATH=".:../lib/twitter4j/twitter4j-core-4.0.4.jar:../lib/twitter4j/twitter4j-stream-4.0.4.jar:../lib/twitter4j/twitter4j-media-support-4.0.4.jar:../lib/twitter4j/twitter4j-async-4.0.4.jar:../lib/twitter4j/twitter4j-examples-4.0.4.jar"

all: main.class

main.class: main.java
	$(JC) -classpath $(CLASSPATH) main.java

clean:
	rm -fr *.class

run:
	java -cp $(CLASSPATH) main ./proxies.dat ./ok_proxies.dat ../controller/cuenta_test.dat ../controller/ok_proxies.dat

