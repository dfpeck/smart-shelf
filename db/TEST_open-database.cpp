#include <iostream>
#include <string>
#include "sqlite3.h"
#include "common.h"

using namespace std;

int main () {
  sqlite3 *db = create_db("TEST_inventory.db");
  sqlite3_close(db);

  return 0;
}
