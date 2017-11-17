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
    "PRIMARY KEY (itypeid)"
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
    "FOREIGN KEY (itemtype) REFERENCES ItemTypes(itypeid)"
    ");";
  const string CREATE_MATS =
    "CREATE TABLE Mats ("
    "matid INTEGER NOT NULL, "
    "PRIMARY KEY (matid)"
    ");";
  const string CREATE_HISTORY =
    "CREATE TABLE History ("
    "item INTEGER NOT NULL, "
    "time DATETIME NOT NULL, "
    "mat INTEGER NOT NULL, "
    "event INTEGER NOT NULL, "
    "sensor1 REAL, "
    "sensor2 REAL, "
    "sensor3 REAL, "
    "sensor4 REAL, "
    // "x REAL NOT NULL, "
    // "y REAL NOT NULL, "
    "CONSTRAINT eventinfo PRIMARY KEY (item, time), "
    "FOREIGN KEY (item) REFERENCES Items(itemid), "
    "FOREIGN KEY (event) REFERENCES EventTypes(eventid)"
    ");";

  const short TABLE_COUNT = 5;
  const string CREATE_TABLES[TABLE_COUNT] = {SQL::CREATE_ITEMTYPES,
                                         SQL::CREATE_EVENTTYPES,
                                         SQL::CREATE_ITEMS,
                                         SQL::CREATE_MATS,
                                         SQL::CREATE_HISTORY};
}
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

/* DATABASE CLASS METHODS */
DbCommon::DbCommon (const string& db_file_init) {
  db_file = db_file_init;
  open();
}

DbCommon::~DbCommon () {
  sqlite3_close(db);
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
    cerr << "SQLite: Can't access " << db_file << ": "
         << sqlite3_errmsg(db) << endl;
    success = false;
  }

  /* Create Tables */
  for (int i=0; i<SQL::TABLE_COUNT; i++) {
    rc = sqlite3_exec(db, SQL::CREATE_TABLES[i].c_str(), NULL, NULL, &errmsg);
    if (rc != SQLITE_OK) {
      cerr << "SQLite: " << errmsg << endl;
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
      cerr << "SQLite: " << errmsg << endl;
      sqlite3_free(errmsg);
      success = false;
    }
  }

  return success;
}

string DbCommon::serialize_records (const string& table,
                                    const string& condition) {
  sqlite3_stmt* stmt;
  int rc1, rc2;
  int column_count;
  stringstream return_str;
  stringstream qry;

  return_str << table << '\n';

  /* Sanitize SQL and Build Query */

  qry << "SELECT * FROM " << table << " WHERE " << condition << ";";

  /* Sanitize SQL */

  /* Prepare the Statement and Bind Parameters */
  rc1 = sqlite3_prepare_v2(db, qry.str().c_str(), -1, &stmt, NULL);
  if (rc1) {
    cerr << "[SQLite] Could not prepare statement, "
         << sqlite3_errmsg(db) << endl;
    return "";
  }

  column_count = sqlite3_column_count(stmt);

  /* Label Columns in Serialization */
  return_str << sqlite3_column_name(stmt, 0);
  for (unsigned int i=1; i<column_count; i++)
    return_str << '\t' << sqlite3_column_name(stmt, i);
  return_str << '\n';

  /* Execute Statement and Serialize Output */
  do {
    rc1 = sqlite3_step(stmt);
    if (rc1 == SQLITE_ROW) {
      for (unsigned int i=0; i<column_count; i++) {
        rc2 = sqlite3_column_type(stmt, i);
        switch (rc2) { // need particular column function based on data type
        case SQLITE_INTEGER:
          return_str << sqlite3_column_int(stmt, i);
          break;
        case SQLITE_FLOAT:
          return_str << sqlite3_column_double(stmt, i);
          break;
        case SQLITE_TEXT:
          return_str << sqlite3_column_text(stmt, i);
          break;
        case SQLITE_NULL:
          break;
        case SQLITE_BLOB:
          break;
        }
        if (i != column_count-1) // if not last column
          return_str << '\t';
      }
      return_str << '\n';
    }
  } while (rc1 != SQLITE_DONE);

  /* Clean Up */
  sqlite3_finalize(stmt);

  return return_str.str();
}

string DbCommon::serialize_records (const string& table) {
  return serialize_records(table, "1=1");
}
