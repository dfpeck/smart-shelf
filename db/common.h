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
  extern const string CREATE_HISTORY;
}
extern const short TABLE_COUNT;
extern const string TABLE_SQL[];

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

/**
 */
vector<string> split_str (const string &str, char delim); // !-- IP

/* RECORD CLASS */
class TableRecord {
  /* Properties */

  /* Accessors */

  /* Serialization */
  /** Convert the record to a string serialization.
   *
   * @return String representing the object data.
   */
  virtual string serialize () =0;
};

class Item; // forward declarations

/** Class for `ItemType` records.
 */
class ItemType : public TableRecord {
  /* Constructors */
public:
  ItemType (int itypeid_init,
            const string& itypename_init,
            bool iscontainer_init);
  /** Constructor
   * Initialize from a serialized object.
   * @param serialized A string returned by `ItemType::serialize`
   */
  ItemType (const string& serialized); // !-- in progress

  /* Properties */
protected:
  int itypeid;
  string itypename;
  bool iscontainer;

  /* Accessors */
public:
  int get_id ();
  string get_name ();
  bool is_container ();

public:
  string serialize () override; // !-- implement
};

/** Class for `EventType` records.
 */
class EventType : public TableRecord { // !-- implement
  /* Properties */
protected:
  int id;
  string name;
public:
  int get_id ();
  string get_name ();

public:
  string serialize () override;
};

/** Class for `History` records.
 */
class Event : public TableRecord { // !-- implement
  /* Properties */
protected:
  Item& item;
  string time;
  EventType& event;
  double sensor1, sensor2, sensor3, sensor4;
  double x, y;
public:
  Item& get_item ();
  string get_time();
  EventType& get_event ();

  /** Return the sensor readings associated with the event.

   * @return Array such that element 0 is the sum of the sensor readings, while
   * elements 1--4 correspond to the sensor of that number.
   */
  array<double, 5> get_sensors();

  /** Return the coordinate position associated with the event.
   *
   * @return (x, y) coordinate pair.
   */
  array<double, 2> get_pos();

public:
  string serialize () override;
};

/** Class for `Items` records.
 */
class Item : public TableRecord { // !-- implement
  /* Properties */
protected:
  int itemid;
  ItemType& itemtype;
  Event& last_history;
public:
  int get_id ();
  ItemType& get_type ();

  /** Return sensor readings produced by the item. Refer to Event::get_sensors.
   */
  array<double, 5> get_sensors ();

  /** Return the coordinate position of the item. Refer to Event::get_pos.
   */
  array<double, 2> get_pos ();

public:
  string serialize () override;
};

/* 

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
  
  /* Constructors */
public:
  DbCommon (const string&);

  /* Accessors */
public:
  string get_db_file();

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
};

#endif

// File local variables for Emacs
// Local Variables:
// mode: c++
// End:
