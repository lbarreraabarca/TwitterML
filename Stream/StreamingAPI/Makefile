JC=javac
CLASSPATH=".:lib/twitter4j/twitter4j-async-4.0.4.jar:lib/twitter4j/twitter4j-core-4.0.4.jar:lib/twitter4j/twitter4j-examples-4.0.4.jar:lib/twitter4j/twitter4j-media-support-4.0.4.jar:lib/twitter4j/twitter4j-stream-4.0.4.jar:lib/json/json-simple-1.1.1.jar:lib/mail/mail-1.4.7.jar:lib/mongodb/mongo-java-driver-2.11.1.jar:lib/slf4j/slf4j-api-1.7.25.jar:lib/slf4j/slf4j-simple-1.7.25.jar"


.java.class:
	$(JC) -classpath $(CLASSPATH) $*.java

CLASSES = \
        mongodbTwitter.java \
        main.java 

default: classes

classes: $(CLASSES:.java=.class)
#all: main.class

main.class: main.java
	$(JC) -classpath $(CLASSPATH) main.java

mongodbTwitter.class: mongodbTwitter.java
	$(JC) -classpath $(CLASSPATH) mongodbTwitter.java


clean:
	rm -fr *.class

run:
	java -cp $(CLASSPATH) main  
#ARG

