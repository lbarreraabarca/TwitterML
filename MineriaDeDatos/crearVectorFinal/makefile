JC=javac
all: main.class

main.class: main.java
	$(JC) main.java

clean:
	rm -fr *.class

run:
	java -d64 -Xms512m -Xmx4g main $(IN) $(WORD) $(OUT) $(TS) $(VERBOS) $(BOW) $(BOH) $(BUM) $(VEST) $(VBOW) $(VBOH) $(VBUM)
