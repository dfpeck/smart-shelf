#include <iostream>
#include <iomanip>
#include "common.h"

int main () {
  DbCommon db("TEST_inventory.db");
  cout << db.serialize_records("EventTypes") << endl;
  cout << db.serialize_records("EventTypes", "eventid=1") << endl;
  cout << db.serialize_records("EventTypes", "eventid=1 OR eventname='ADDED'");
  return 0;
}
