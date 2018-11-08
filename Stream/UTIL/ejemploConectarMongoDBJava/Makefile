JC=javac
classpath=".:lib/mongo-java-driver-2.11.1.jar"
all: main.class

main.class: main.java
	$(JC) -classpath $(classpath) main.java

clean:
	rm -fr *.class

run:
	java -cp $(classpath)  main
