#include <iostream>
#include <cassert>
#include <string>
#include <sstream>
#include <array>
#include <vector>
#include "sqlite3.h"
#include "common.h"

using namespace std;

/* CONSTANTS */
namespace SQL{
  const string CREATE_ITEMTYPES =
    "CREATE TABLE ItemTypes ("
    "itypeid INTEGER NOT NULL, "
    "itypename STRING, "
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
    "itemtype INTEGER NOT NULL, "
    "PRIMARY KEY (itemid), "
    "FOREIGN KEY (type) REFERENCES ItemTypes(typeid)"
    ");";
  const string CREATE_HISTORY =
    "CREATE TABLE History ("
    "item INTEGER NOT NULL, "
    "time DATETIME NOT NULL, "
    "event INTEGER NOT NULL, "
    "sensor1 REAL NOT NULL, "
    "sensor2 REAL NOT NULL, "
    "sensor3 REAL NOT NULL, "
    "sensor4 REAL NOT NULL, "
    // "x REAL NOT NULL, "
    // "y REAL NOT NULL, "
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

const char SERIALSEP = '\t';

/* HELPER/UTILITY FUNCTIONS */
bool fcheck (const string& fname) {
  FILE* file;
  bool return_value = false;

  file = fopen(fname.c_str(), "r"); // !-- slow, consider other methods
  if (file) {
    fclose(file);
    return_value = true;
  }

  return return_value;
}

vector<string> split_str (const string &str, char delim) {
  stringstream str_ss(str);
  string item;
  vector<string> tokens;
  while (getline(str_ss, item, delim))
    tokens.push_back(item);
  return tokens;
}

/* ITEMTYPE CLASS */
ItemType::ItemType (int itypeid_init,
                    const string& itypename_init,
                    bool iscontainer_init) {
  itypeid = itypeid_init;
  itypename = itypename_init;
  iscontainer = iscontainer_init;
}

ItemType::ItemType (const string& serialized) {
  vector<string> tokens = split_str(serialized, SERIALSEP);
  assert(tokens.size() == 3); // !-- replace with exception
  assert(tokens[2] == "0" || tokens[2] == "1");
  itypeid = stoi(tokens[0]);
  itypename = tokens[1];
  iscontainer = (tokens[2] == "1");
 }

int ItemType::get_id () {
  return this->itypeid;
}

string ItemType::get_name () {
  return this->itypename;
}

bool ItemType::is_container () {
  return this->iscontainer;
}

string ItemType::serialize () {
  stringstream ret_str;
  ret_str << itypeid << SERIALSEP
          << itypename << SERIALSEP
          << iscontainer << SERIALSEP;
  return ret_str.str();
}

/* DATABASE CLASS METHODS */
DbCommon::DbCommon (const string& db_file_init) {
  db_file = db_file_init;
  open();
}

bool DbCommon::open () {
  int rc; // return code

  if (!fcheck(db_file)) {
    create();
  }
  else {
    rc = sqlite3_open(db_file.c_str(), &db);
    if (rc) {
      cerr << "Can't access" << db_file << ": " << sqlite3_errmsg(db) << endl;
      return false;
    }
  }
  return true;
}

bool DbCommon::create () {
  string stmt;
  char* errmsg;
  int rc; // return code
  bool success = true;

  /* Create Database File */
  rc = sqlite3_open(db_file.c_str(), &db);
  if (rc) {
    cerr << "Can't access " << db_file << ": " << sqlite3_errmsg(db) << endl;
    success = false;
  }

  /* Create Tables */
  for (int i=0; i<TABLE_COUNT; i++) {
    rc = sqlite3_exec(db, TABLE_SQL[i].c_str(), NULL, NULL, &errmsg);
    if (rc != SQLITE_OK) {
      cerr << "SQL error: " << errmsg << endl;
      sqlite3_free(errmsg);
      success = false;
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
      success = false;
    }
  }

  return success;
}
