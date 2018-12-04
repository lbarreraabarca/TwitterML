JC=javac
all: main.class

main.class: main.java
	$(JC) main.java

clean:
	rm -fr *.class

run:
	java main $(IN) $(OUTPUT_FILE_TRAIN) $(OUTPUT_FILE_TEST) $(TRAIN_PERCENT)
