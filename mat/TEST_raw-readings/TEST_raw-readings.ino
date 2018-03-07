#include "HX711.h"
#include <stdlib.h>

const int MAT_LENGTH = 48;
const int MAT_HEIGHT = 20;

//HX711 constructor (dout pin, sck pin)
HX711 load_cell[4]; //Load cell array

long t;
float readings[4], total_weight, x_coord, y_coord;

void setup() {
  Serial.begin(38400);
  //Initialize Load cell objects
  load_cell[0].begin(A0, 2);  //Top right corner
  load_cell[1].begin(A1, 3);  //Bottom right corner
  load_cell[2].begin(A2, 4);  //Bottom left
  load_cell[3].begin(A3, 5);  //Top left
  
  total_weight = 0;
  x_coord = 0;
  y_coord = 0;
  t = 0;

  for (int i = 0; i <4; i++)  { //Initialize scale factor and zero scale
      load_cell[i].set_scale(1001);
      load_cell[i].tare(20);
      readings[i] = 0;
   }
}

void loop() {
  if (t > millis()+500) {
    String output = "";
    
    for (int i = 0; i <4; i++) {
      readings[i] = load_cell[i].get_units(5);
      total_weight += readings[i];
    }
  
    x_coord = ((MAT_LENGTH/2)*(readings[0]+readings[1]-readings[2]-readings[3]))/MAT_LENGTH;
    y_coord = ((MAT_HEIGHT/2)*(readings[0]+readings[3]-readings[1]-readings[2]))/MAT_HEIGHT;
    
    for (int i = 0; i <4; i++) {
      output = "Sensor ";
      Serial.println(output + (int)(i+1) + " reading: " + readings[i]);
    }
  
    output = "Total weight: ";
    Serial.println(output + total_weight);
    
    output = "Coordinates: ";
    Serial.println(output + x_coord + ", " + y_coord);
    t = millis();
  }
}
