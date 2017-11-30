CXX = g++ -g -std=c++11
CC = gcc

JC = javac
JFLAGS = -classpath ".:/usr/share/java/h2.jar"

DOX = doxygen

.PHONY: all check clean

test: db/TEST_Db

check: 

# Binaries #
db/TEST_Db: db/TEST_Db.java db/Db.class
	$(JC) $(JFLAGS) db/TEST_Db.java

db/Db.class: db/Db.java
	$(JC) $(JFLAGS) db/Db.java

# Docmentation #
docs:
	rm -r html; $(DOX); rm -r latex

# Operations #
clean:
	rm -f $(BIN) */*.o
