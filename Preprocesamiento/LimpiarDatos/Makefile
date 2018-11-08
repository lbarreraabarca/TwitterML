JC=javac
CLASSPATH=".:./json-simple-1.1.1.jar"

all: main.class

main.class: main.java
	$(JC) -classpath $(CLASSPATH) main.java

clean:
	rm -fr *.class

run:
	java -cp $(CLASSPATH) main $(IN) $(OUT) $(STOPWORDS) 
