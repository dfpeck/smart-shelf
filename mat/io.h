#ifndef IO_H
#define IO_H

/** Append sensor change data to the log.
 *
 * @param time The time at which the data was read.
 * @param sensorX_d The change in sensorX from its previous reading.
 */
bool update_sensor_d_log (int time,
                          int sensor1_d, int sensor2_d,
                          int sensor3_d, int sensor4_d);

#endif
