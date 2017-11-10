#include <iostream>
#include <iomanip>
#include "common.h"

int main () {
  bool flag = false;
  ItemType test1(1, "Test 1", false),
    testcont(2, "Test Container", true),
    testout = test1;
  ItemType recipient(test1.serialize());
 print:
  cout << setw(12) << left <<  "ID" << right << ":"
       << setw(20) << testout.get_id()
       << setw(20) << recipient.get_id()
       << endl
       << setw(12) << left << "Name" << right << ":"
       << setw(20) << testout.get_name()
       << setw(20) << recipient.get_name()
       << endl
       << setw(12) << left << "Container" << right << ":"
       << setw(20) << testout.is_container()
       << setw(20) << recipient.is_container()
       << endl;
  if (!flag) {
    flag = true;
    cout << endl;
    recipient = ItemType(testcont.serialize());
    testout = testcont;
    goto print;
  }

  return 0;
}
