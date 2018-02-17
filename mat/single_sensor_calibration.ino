

#include "HX711.h"
#include <stdlib.h>

//HX711 constructor (dout pin, sck pin)
HX711 load_cell; //Load cell



const float weight_threshold = .05;
const float calibration_weight = 5;

long t;
int scale_factor;
bool calibrated;

void setup() {
  Serial.begin(38400);
  Serial.println("Wait...");
  
  load_cell.begin(A2, 4); //Initialize Load cell
  
  t=0;
  calibrated = false;

  //Load cell tare
  scale_factor = 10000;
  load_cell.set_scale(10000);
  load_cell.tare(20);
  
  Serial.println("Startup + tare is complete");
  Serial.println("Calibration beginning. Place 5lb at center of mat.");
}

void loop() {
  double temp_reading = load_cell.get_units(2);
  
  if (calibrated == false) {
    while (load_cell.get_units(5) < weight_threshold) {  //Wait for weight to be put on mat
      delay(50); }
    
    while (abs(load_cell.get_units(5) - temp_reading) > weight_threshold) {  //Wait for sensor readings to stabilize
      delay(50);
      temp_reading = load_cell.get_units(5);
    }
    
    while (abs(load_cell.get_units(5) - calibration_weight) > weight_threshold) //Run calibration loop
      {
        if (temp_reading > calibration_weight)
          scale_factor += 100;
        else if (temp_reading < calibration_weight)
          scale_factor += -100;

        delay(250);
        load_cell.set_scale(scale_factor);
        temp_reading = load_cell.get_units(2);
        Serial.print("Scale factor: ");
        Serial.println(scale_factor);
        Serial.print("Weight: ");
        Serial.println(temp_reading);
        
      }
      
     Serial.print("Calibration Complete.\nFinal scale factor: ");
     Serial.println(scale_factor);
     calibrated = true;
      //Serial.print("Weight: ");
      //Serial.println(load_cell.get_units(5));
      //delay(500);
      
  }
  Serial.print("Weight: ");
  Serial.println(load_cell.get_units(2));
  delay(250);
}
