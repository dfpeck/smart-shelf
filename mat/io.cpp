#include <Arduino.h>
#include <SD.h>
// #include <sstream> // uncomment this line if using version B of
//                    // `update_sensor_d_log`
#include "io.h"

// version A, directly writes everything
bool update_sensor_d_log (int time,
                          int sensor1_d, int sensor2_d,
                          int sensor3_d, int sensor4_d) {
  File sensor_d_log = SD.open("sensor_d.csv", FILE_WRITE);
  if (sensor_d_log) {
    sensor_d_log.print(time);
    sensor_d_log.print(',');
    sensor_d_log.print(sensor1_d);
    sensor_d_log.print(',');
    sensor_d_log.print(sensor2_d);
    sensor_d_log.print(',');
    sensor_d_log.print(sensor3_d);
    sensor_d_log.print(',');
    sensor_d_log.print(sensor4_d);
    sensor_d_log.println();
  }
  else {
    Serial.println("error opening sensor_d.csv");
  }
}

// version B, build string and then write
// more readable, but requires sstream
// bool update_sensor_d_log (int time,
//                           int sensor1_d, int sensor2_d,
//                           int sensor3_d, int sensor4_d) {
//   std::stringstream data;
//   data << time << ','
//        << sensor1_d << ',' << sensor2_d << ','
//        << sensor3_d << ',' << sensor4_d;

//   File sensor_d_log = SD.open("sensor_d.csv", FILE_WRITE);
//   if (sensor_d_log)
//     sensor_d_log.println(data);
//   else
//     Serial.println("error opening sensor_d.csv");
// }
