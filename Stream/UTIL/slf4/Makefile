JC=javac
classpath=".:slf4j/slf4j-api-1.7.25.jar:slf4j/slf4j-simple-1.7.25.jar"
all: main.class

main.class: main.java
	$(JC) -classpath $(classpath) main.java

clean:
	rm -fr *.class

run:
	java -cp $(classpath)  main
