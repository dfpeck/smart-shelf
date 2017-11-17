/** @file common.h
 * Base class for code used by both the mat and the server.
 */

#ifndef DB_COMMON_H
#define DB_COMMON_H

#include <string>
#include <array>
#include <vector>
#include "sqlite3.h"

using namespace std;

/* CONSTANTS */
/** Data and functions for manipulating SQL code. */
namespace SQL {
  extern const string CREATE_ITEMTYPES;
  extern const string CREATE_EVENTTYPES;
  extern const string CREATE_ITEMS;
  extern const string CREATE_MATS;
  extern const string CREATE_HISTORY;
  extern const short TABLE_COUNT;
  extern const string CREATE_TABLES[];
}

/* DB Contents */
extern const short EVENT_COUNT;
extern const string EVENT_TYPES[];

/* Serialization */
extern const char SERIALSEP;


/* HELPER FUNCTIONS */
/** Check whether a file is read-write accessible.
 * 
 * @param fname Name of the file to check.
 * @return Boolean representing the file's rw-accessibility.
 */
bool fcheck (const string &fname);

/** Split a string into substrings deliminated by a character.
 *
 * @param str The string to split.
 * @param delim The deliminator dividing the substrings.
 */
vector<string> split_str (const string &str, char delim);


/* COMMON DATABASE CLASS */
class DbCommon {
  /* Properties */
protected:
  /**
   * The SQLite database connection.
   */
  sqlite3* db;

  /**
   * Path to the SQLite database file.
   */
  string db_file;
  
  /* Constructors and Destructor */
public:
  DbCommon (const string&);

  ~DbCommon ();

  /* Accessors */
public:
  string get_db_file();

  /* Database Initialization */
protected:
  /**
   * Create a new database file and initialize data.
   *
   * @param fname Filename to give the databse.
   * @return `true` if creation succeeds without error, `false` otherwise.
   */
  bool create ();

  /**
   * Open the database in the given file, creating one if it does not exist.
   *
   * @param fname Path to the database file.
   * @return Success or failure.
   */
  bool open ();

  /* Record Serialization */
  /** Retrive one or more records from the database and serialize as a string.
   *
   * @param table The table to query.
   * @param condition Conditions restricting which records are retrieved. Must
   * be valid as the conditional component of an SQL `WHERE` clause. This
   * parameter is optional.
   * @return A string representing the records retrieved; NULL if any step of
   * the process has failed.
   */
public:
  string serialize_records (const string& table, const string& condition);
  string serialize_records (const string& table);
  // !-- IP

  string insert_from_serialization (const string& serialized); // !-- todo
};

#endif

// File local variables for Emacs
// Local Variables:
// mode: c++
// End:
