#include "HX711.h"
#include <stdlib.h>

//HX711 constructor (dout pin, sck pin)
HX711 load_cell[4]; //Load cell array

const int MAT_LENGTH = 24;            //Length of mat in inches
const int MAT_HEIGHT = 20;            //Height of mat in inches
const float WEIGHT_THRESHOLD = .1;    //Threshold to determine change in mat weight
const float CALIBRATION_WEIGHT = 72;  //How much weight to put on the mat for calibration

struct readings {
  float reading[4];
};

long t;               //Time
int scale_factor[4];  //Array of scale factors for calibration
int stable_timer; //Incrimenter for num of loops before stable is set to true
float current_weight; //Current weight on mat
readings previous, stable; //readings from previous loop, readings when weight becomes unstable
bool is_stable;

void setup() {
  int cal_inc = 0;
  
  Serial.begin(38400);
  Serial.println("Wait...");
  
  //Initialize Load cell objects
  load_cell[0].begin(3, 2);  //Top left corner
  load_cell[1].begin(6, 5);  //Top right corner
  load_cell[2].begin(9, 8);  //Bottom right
  load_cell[3].begin(13, 12);  //Bottom left
  
  t=0;
  is_stable = true;
  
  //Load cell tare
  for (int i = 0; i <4; i++)  { //Initialize scale factor and zero scale
      
      scale_factor[i] = 100.1;
      load_cell[i].set_scale(scale_factor[i]);
      load_cell[i].tare(10);
      previous.reading[i] = 0;
      stable.reading[i] = 0;
    }
  Serial.println("Startup + tare is complete");

  //Calibration
  double temp_reading = 0;
  Serial.println("Calibration beginning. Place 5lb at center of mat.");
    
    while (load_cell[0].get_units(5) < WEIGHT_THRESHOLD) {  //Wait for weight to be put on mat
      delay(50); }
      Serial.println("Weight on mat");
      
    while (cal_inc < 10) {  //wait for sensor readings to stabilize
      if (abs(load_cell[0].get_units(5) - temp_reading) < WEIGHT_THRESHOLD)
        cal_inc++;
      delay(50);
      temp_reading = load_cell[0].get_units(5);
    }

    Serial.println("Readings stable");
    
    for (int i = 0; i <4; i++)  {   //Run calibration loop for all sensors
      temp_reading = load_cell[i].get_units(2);
      while (abs(temp_reading - CALIBRATION_WEIGHT/4) > WEIGHT_THRESHOLD) //Loop until reading is close enough to actual value
      {
        temp_reading = load_cell[i].get_units(2);
        
        scale_factor[i] *= 4 *temp_reading/CALIBRATION_WEIGHT;
        
        load_cell[i].set_scale(scale_factor[i]);
       
      }      
    }
    Serial.println("Calibration done.");
    while (abs(load_cell[0].get_units(3)) > WEIGHT_THRESHOLD) {}
   
}

void loop() {
  readings current, delta; //Current sensor readings and change in readings
  float stable_difference, recent_difference; //the difference in sensor readings since last period of stability and the difference between the current and last loop
  current_weight = 0;
  recent_difference = 0;
  stable_difference = 0;

  if (millis() > t+50) {
    //Serial.print("Current readings: ");
    for (int i = 0; i < 4; i++) { //Loop to get sensor readings and put them into readings array
      current.reading[i] = load_cell[i].get_units(3);
      current_weight+=current.reading[i];
      delta.reading[i] = current.reading[i] - previous.reading[i];
      recent_difference+=delta.reading[i];
      stable_difference += current.reading[i] - stable.reading[i];
      previous.reading[i] = current.reading[i];
    }
    
    for (int i = 0; i < 3; i++)
    {
      Serial.print(current.reading[i]);
      Serial.print(",");
    }
    Serial.println(current.reading[3]);
    
    if (is_stable) {
      if (abs(recent_difference) > WEIGHT_THRESHOLD) {
        is_stable = false;
      }
    }
    else {
      if (abs(recent_difference) < WEIGHT_THRESHOLD) {
        stable_timer++;
      }
      if (stable_timer > 4){
        is_stable = true;
        stable_timer = 0;
//        Serial.print("\t");
//        for (int i = 0; i < 3; i++) {
//          Serial.print(current.reading[i] - stable.reading[i]);
//          Serial.print(",");
//        }
//        Serial.print(current.reading[3] - stable.reading[3]);
//        Serial.println("\t");
        stable = current;
      }
    }
  t = millis();
  }
}
