CXX = g++ -g -std=c++11
CC = gcc

JC = javac
JFLAGS = -classpath ".:/usr/share/java/h2.jar"

DOX = doxygen

.PHONY: all clean

# Docmentation #
docs:
	rm -r html; $(DOX); rm -r latex

# Operations #
clean:
	rm -f $(BIN) */*.o */*.class
