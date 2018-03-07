#include "HX711.h"
#include <stdlib.h>

//HX711 constructor (dout pin, sck pin)
HX711 load_cell[4]; //Load cell array

const int MAT_LENGTH = 48;            //Length of mat in inches
const int MAT_HEIGHT = 20;            //Height of mat in inches
const float WEIGHT_THRESHOLD = .5;    //Threshold to determine change in mat weight
const float CALIBRATION_WEIGHT = 50;  //How much weight to put on the mat for calibration


long t;               //Time
int scale_factor[4];  //Array of scale factors for calibration
float current_weight; //Current weight on mat

void setup() {
  Serial.begin(38400);
  Serial.println("Wait...");
  
  //Initialize Load cell objects
  load_cell[0].begin(A0, 2);  //Top left corner
  load_cell[1].begin(A1, 3);  //Top right corner
  load_cell[2].begin(A2, 4);  //Bottom right
  load_cell[3].begin(A3, 5);  //Bottom left
  
  t=0;
  
  //Load cell tare
  for (int i = 0; i <4; i++)  { //Initialize scale factor and zero scale
      scale_factor[i] = 1001;
      load_cell[i].set_scale(scale_factor[i]);
      load_cell[i].tare(20);
    }
  Serial.println("Startup + tare is complete");
  
  double temp_reading = 0;
  Serial.println("Calibration beginning. Place 5lb at center of mat.");
    
    while (load_cell[0].get_units(5) < WEIGHT_THRESHOLD) {  //Wait for weight to be put on mat
      delay(50); }
      Serial.println("Weight on mat");
      
    while (abs(load_cell[0].get_units(5) - temp_reading) > WEIGHT_THRESHOLD) {  //wait for sensor readings to stabilize
      delay(100);
      temp_reading = load_cell[0].get_units(5);
    }

    Serial.println("Readings stable");
    
    for (int i = 0; i <4; i++)  {   //Run calibration loop for all sensors
      temp_reading = load_cell[i].get_units(2);
      while (abs(temp_reading - CALIBRATION_WEIGHT/4) > WEIGHT_THRESHOLD) //Loop until reading is close enough to actual value
      {
        temp_reading = load_cell[i].get_units(2);
        if (temp_reading > CALIBRATION_WEIGHT/4)        //Increases scale factor if reading is above actual value
          scale_factor[i] += 100;
        else if (temp_reading < CALIBRATION_WEIGHT/4)   //Decreases scale factor if reading is below actual value
          scale_factor[i] += -100;
        
        load_cell[i].set_scale(scale_factor[i]);
      }
      Serial.println("Calibration done.");
    }
}

void loop() {
  float readings[4]; //Arrays for sensor readings
  String output = ""; //String for output
  current_weight = 0;

  if (t > millis()+500); {
    
    for (int i = 0; i < 4; i++) { //Loop to get sensor readings and put them into readings array
      readings[i] = load_cell[i].get_units(3);
      output = "Sensor ";
      Serial.println(output + (int)(i+1) + "reading: " + readings[i]);
      current_weight+=readings[i];
    }
    
    output = "Total Weight: ";
    Serial.println(output + current_weight);
  }
  
}
