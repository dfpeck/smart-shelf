/** @file common.h
 * Base class for code used by both the mat and the server.
 */

#ifndef DB_COMMON_H
#define DB_COMMON_H

#include <string>
#include "sqlite3.h"

using namespace std;

/* CONSTANTS */

/** Data and functions for manipulating SQL code. */
namespace SQL {
  extern const string CREATE_ITEMTYPES;
  extern const string CREATE_EVENTTYPES;
  extern const string CREATE_ITEMS;
  extern const string CREATE_HISTORY;
}
extern const short TABLE_COUNT;
extern const string TABLE_SQL[];

/* DB Contents */
extern const short EVENT_COUNT;
extern const string EVENT_TYPES[];

/* FUNCTIONS */

/**
 * Check whether a file is read-write accessible.
 * 
 * @param fname Name of the file to check.
 * @return Boolean representing the file's rw-accessibility.
 */
bool fcheck (const string &fname);

/**
 * Open the database in the given file, creating one if it does not exist.
 *
 * @param fname Path to the database file.
 * @return The sqlite3 database connection.
 */
sqlite3* open_db (const string &fname);

/**
 * Create a new database file and initialize data.
 *
 * @param fname Filename to give the databse.
 * @return The sqlite3 database connection.
 */
sqlite3* create_db (const string &fname);

#endif
