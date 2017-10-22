cxx = g++ -g -std=c++11
cc = gcc

lib = -lpthread -ldl

TEST_create-database: TEST_create-database.cpp db-common.o sqlite3.o
	$(cxx) -o $@ $^ $(lib)

db-common.o: db-common.cpp sqlite3.o
	$(cxx) -o $@ -c $^ $(lib)
sqlite3.o: sqlite3.c
	$(cc) -o $@ -c $^

clean:
	rm *.o
