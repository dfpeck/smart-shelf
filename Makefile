CXX = g++ -g -std=c++11
CC = gcc
DOX = doxygen

LIB = -lpthread -ldl

# Binaries #
TEST_open-database: db/TEST_open-database.cpp db/common.o db/sqlite3.o
	$(CXX) -o $@ $^ $(LIB)

db/common.o: db/common.cpp db/sqlite3.o
	$(CXX) -o $@ -c $^ $(LIB)
db/sqlite3.o: db/sqlite3.c
	$(CC) -o $@ -c $^

# Docmentation #
docs:
	rm -r html; $(DOX); rm -r latex

# Operations #
clean:
	rm */*.o
