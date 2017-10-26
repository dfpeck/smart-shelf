#include <iostream>
#include <string>
#include "sqlite3.h"
#include "common.h"

using namespace std;

/** CONSTANTS **/
namespace SQL{
  const string CREATE_ITEMTYPES =
    "CREATE TABLE ItemTypes ("
    "typeid INTEGER NOT NULL, "
    "typename STRING, "
    "iscontainer INTEGER, "
    "PRIMARY KEY (typeid)"
    ");";
  const string CREATE_EVENTTYPES =
    "CREATE TABLE EventTypes ("
    "eventid INTEGER NOT NULL, "
    "eventname STRING NOT NULL, "
    "PRIMARY KEY (eventid)"
    ");";
  const string CREATE_ITEMS =
    "CREATE TABLE Items ("
    "itemid INTEGER NOT NULL, "
    "type INTEGER NOT NULL, "
    "PRIMARY KEY (itemid), "
    "FOREIGN KEY (type) REFERENCES ItemTypes(typeid)"
    ");";
  const string CREATE_HISTORY =
    "CREATE TABLE History ("
    "item INTEGER NOT NULL, "
    "time DATETIME NOT NULL, "
    "event DATETIME NOT NULL, "
    "sensor1 REAL NOT NULL, "
    "sensor2 REAL NOT NULL, "
    "sensor3 REAL NOT NULL, "
    "sensor4 REAL NOT NULL, "
    "x REAL NOT NULL, "
    "y REAL NOT NULL, "
    "CONSTRAINT eventinfo PRIMARY KEY (item, time), "
    "FOREIGN KEY (item) REFERENCES Items(itemid), "
    "FOREIGN KEY (event) REFERENCES EventTypes(eventid)"
    ");";
}
const short TABLE_COUNT = 4;
const string TABLE_SQL[TABLE_COUNT] = {SQL::CREATE_ITEMTYPES,
                                       SQL::CREATE_EVENTTYPES,
                                       SQL::CREATE_ITEMS,
                                       SQL::CREATE_HISTORY};
const short EVENT_COUNT = 6;
const string EVENT_TYPES[EVENT_COUNT] = {"ADDED", "REMOVED", "REPLACED",
                                         "REDUCED", "REFILLED", "SLID"};

/** HELPER/UTILITY FUNCTIONS **/
bool fcheck (const string &fname) {
  FILE *file;
  bool return_value = false;

  file = fopen(fname.c_str(), "r");
  cout << "File: " << file << endl;
  if (file) {
    fclose(file);
    return_value = true;
  }

  return return_value;
}

/** DATABASE FUNCTIONS **/
sqlite3* open_db (const string &fname) {
  sqlite3 *db; //database
  int rc;

  cout << "TEST open_db" << endl;
  if (!fcheck(fname)) {
    db = create_db(fname);
  }
  else {
    rc = sqlite3_open(fname.c_str(), &db);
    if (rc)
      cerr << "Can't access" << fname << ": " << sqlite3_errmsg(db) << endl;
  }

  return db;
}

sqlite3* create_db (const string &fname) {
  sqlite3 *db; // database connection
  string stmt;
  char *errmsg;
  int rc; // return code

  /* Create Database File */
  rc = sqlite3_open(fname.c_str(), &db);
  if (rc) {
    cerr << "Can't access " << fname << ": " << sqlite3_errmsg(db) << endl;
    return db;
  }

  /* Create Tables */
  for (int i=0; i<TABLE_COUNT; i++) {
    rc = sqlite3_exec(db, TABLE_SQL[i].c_str(), NULL, NULL, &errmsg);
    if (rc != SQLITE_OK) {
      cerr << "SQL error: " << errmsg << endl;
      sqlite3_free(errmsg);
    }
  }

  /* Add EventTypes */
  for (int i=0; i<EVENT_COUNT; i++) {
    stmt = "INSERT INTO EventTypes\nVALUES (" + to_string(i) + ", '"
      + EVENT_TYPES[i] + "');";
    rc = sqlite3_exec(db, stmt.c_str(), NULL, NULL, &errmsg);
    if (rc != SQLITE_OK) {
      cerr << "SQL error: " << errmsg << endl;
      sqlite3_free(errmsg);
    }
  }

  return db;
}
