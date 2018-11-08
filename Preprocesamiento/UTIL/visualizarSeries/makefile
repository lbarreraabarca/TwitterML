JC=javac
JAVA=java
SRC=main

all: $(SRC).class

$(SRC).class: $(SRC).java
	$(JC) $(SRC).java

run:
	$(JAVA) $(SRC) $(in) $(word) $(output) $(models) 
clean:
	rm *.class
