JC=javac
all: main.class
CLASSPATH=".:lib/javaml-0.1.7/javaml-0.1.7.jar"

main.class: main.java
	$(JC) -classpath $(CLASSPATH) main.java

clean:
	rm -fr *.class

run:
	java -cp $(CLASSPATH) main $(IN) $(OUT) $(BOLSA) $(AZAR) $(IG) $(FS)
