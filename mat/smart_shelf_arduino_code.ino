#include "HX711.h"
#include <stdlib.h>

//HX711 constructor (dout pin, sck pin)
HX711 load_cell[4]; //Load cell array

const int mat_length = 48;
const int mat_height = 20;
const float weight_threshold = .05;
const float calibration_weight = 5;

//Struct for each item on the mat containing an array for coordinate locations (0 is x, 1 is y), and a variable for weight
struct mat_item { 
float x_coord, y_coord, weight, readings[4];
};

long t;
int scale_factor[4];
bool calibrated;
int num_of_items;
float previous_weight; //Current/Previous total weight on mat
float current_weight;
int stable;
mat_item items[2];

void setup() {
  Serial.begin(38400);
  Serial.println("Wait...");
  
  //Initialize Load cell objects
  load_cell[0].begin(A0, 2);  //Top left corner
  load_cell[1].begin(A1, 3);  //Top right corner
  load_cell[2].begin(A2, 4);  //Bottom right
  load_cell[3].begin(A3, 5);  //Bottom left
  
  t=0;
  calibrated = false;
  
  //Load cell tare
  for (int i = 0; i <4; i++)  { //Initialize scale factor and zero scale
      scale_factor[i] = 10000;
      load_cell[i].set_scale(10000);
      load_cell[i].tare(20);
    }
  Serial.println("Startup + tare is complete");
}

void loop() {
  float readings[4],temp; //Arrays for sensor readings, temp variable for 
  char data[40];
  
  double temp_reading = 0;    //Cablibration code start
  
  if (calibrated == false) {
    Serial.println("Calibration beginning. Place 5lb at center of mat.");
    
    while (load_cell[0].get_units(5) < weight_threshold) {  //Wait for weight to be put on mat
      delay(50); }
      Serial.println("Weight on mat");
      
    while (abs(load_cell[0].get_units(5) - temp_reading) > weight_threshold) {  //wait for sensor readings to stabilize
      delay(100);
      temp_reading = load_cell[0].get_units(5);
    }

    Serial.println("Readings stable");
    
    for (int i = 0; i <4; i++)  { //Run calibration loop for all sensors
      temp_reading = load_cell[i].get_units(2);
      while (abs(temp_reading - calibration_weight/4) > weight_threshold)
      {
        temp_reading = load_cell[i].get_units(2);
        if (temp_reading > calibration_weight/4)
          scale_factor[i] += 100;
        else if (temp_reading < calibration_weight/4)
          scale_factor[i] += -100;
        
        load_cell[i].set_scale(scale_factor[i]);

        Serial.print("Scale factor ");
        Serial.print(i);
        Serial.print(": ");
        Serial.println(scale_factor[i]);
        Serial.print("Weight");
        Serial.print(i);
        Serial.print(": ");
        Serial.println(temp_reading);
      }
    } 
    calibrated = true;
    Serial.println("Calibration done.");        //Cablibration code end
  }
    

    
      current_weight = 0;
      for (int i = 0; i < 4; i++) { //Loop to get sensor readings and put them into readings array
        readings[i] = load_cell[i].get_units(3);
        Serial.print(i);
        Serial.print(". ");
        Serial.println(readings[i]);
        current_weight+=readings[i];
      }

      Serial.print("Total Weight: ");
      Serial.println(current_weight);

      


      
      
//      switch (num_of_items) {
//        
//        case 0:   //No items on mat
//          if (current_weight > weight_threshold) //Item placed on mat
//          {
//            temp = 0;
//            while (abs(current_weight - temp) > weight_threshold)
//            {
//              temp = current_weight;
//              current_weight = 0;
//              for (int i = 0; i < 4; i++) { //Loop to get sensor readings and put them into readings array
//                readings[i] = load_cell[i].get_units(3);
//                current_weight+=readings[i];
//              }
//            }
//            if (current_weight > weight_threshold) {
//              for (int i = 0; i < 4; i++)
//                items[0].readings[i] = readings[i];
//                
//              items[0].weight = current_weight;
//              if (previous_weight > 0)
//              {
//                items[0].x_coord = ((readings[2] + readings[3])*mat_length)/current_weight;
//                items[0].y_coord = ((readings[0] + readings[3])*mat_length)/current_weight;
//              }
//              else {
//                 items[0].x_coord = 0;
//                 items[0].y_coord = 0;
//              }
//              
//              num_of_items = 1;
//              break;
//            }
//          }
//          Serial.print("No Items.");
//          break;
//          
//        case 1:   //Single item on mat
//        
//          if (abs(current_weight - current_weight) > weight_threshold) {
//            temp = 0;
//            while (abs(current_weight - temp) > weight_threshold)
//            {
//              temp = current_weight;
//              current_weight = 0;
//              for (int i = 0; i < 4; i++) { //Loop to get sensor readings and put them into readings array
//                readings[i] = load_cell[i].get_units(3);
//                current_weight+=readings[i];
//              }
//            }
//            if (abs(current_weight - current_weight) > weight_threshold) {
//              items[1].weight = current_weight - items[0].weight;
//              for (int i = 0; i < 4; i++)
//                items[1].readings[i] = readings[i] - items[0].readings[i];
//              
//              items[1].weight = current_weight - items[0].weight;
//              if (current_weight > 0)
//              {
//                items[1].x_coord = ((items[1].readings[2] + items[1].readings[3])*mat_length)/items[1].weight;
//                items[1].y_coord = ((items[1].readings[0] + items[1].readings[3])*mat_length)/items[1].weight;
//              }
//              else {
//                 items[1].x_coord = 0;
//                 items[1].y_coord = 0;
//              }
//              
//              num_of_items = 2;
//            }
//          }
//          
//          
//          sprintf(data, "%f,%f,%f\n", items[0].x_coord, items[0].y_coord, items[0].weight);
//          break;
//          
//        case 2:   //2 items on mat
//          if (abs(current_weight - current_weight) > weight_threshold) {  //Weight values changing
//            temp = 0;
//            while (abs(current_weight - temp) > weight_threshold)
//            {
//              temp = current_weight;
//              current_weight = 0;
//              for (int i = 0; i < 4; i++) { //Loop to get sensor readings and put them into readings array
//                readings[i] = load_cell[i].get_units(3);
//                current_weight+=readings[i];
//              }
//            }
//            if (abs(current_weight - items[1].weight) < weight_threshold) { //1st Item removed
//              items[0].weight = items[1].weight;
//              items[0].x_coord = items[1].x_coord;
//              items[0].y_coord = items[1].y_coord;
//              for (int i = 0; i < 4; i++)
//                items[0].readings[i] = items[1].readings[i];
//              num_of_items = 1;
//            }
//            if (abs(current_weight - items[0].weight) < weight_threshold) { //2nd Item removed
//              num_of_items = 1;
//            }
//          }
//          sprintf(data, "%f,%f,%f,%f,%f,%f\n", items[0].x_coord, items[0].y_coord, items[0].weight,items[1].x_coord, items[1].y_coord, items[1].weight);
//          break;
//        
//      }
}
