/* Database code used by both the mat and the server. */

#ifndef DB_INIT
#define DB_INIT

#include <string>
#include "sqlite3.h"

using namespace std;

/** CONSTANTS **/
/* SQL Code */
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

/** FUNCTIONS **/
sqlite3* create_db (const string &fname);
/* Create a new database file
 *
 * ARGUMENTS
 * `fname`: name for the database file
 * RETURNS: sqlite3 database connection
 */

#endif
