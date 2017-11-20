CXX = g++ -g -std=c++11
CC = gcc
JAVA = javac
DOX = doxygen

BIN = 

.PHONY: all check clean

all: 

check: 

# Binaries #

# Docmentation #
docs:
	rm -r html; $(DOX); rm -r latex

# Operations #
clean:
	rm -f $(BIN) */*.o
