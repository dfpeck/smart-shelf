#include <stdlib.h>

const int MAT_LENGTH = 48;            //Length of mat in inches
const int MAT_HEIGHT = 20;            //Height of mat in inches
const float WEIGHT_THRESHOLD = .5;    //Threshold to determine change in mat weight
const float CALIBRATION_WEIGHT = 50;  //How much weight to put on the mat for calibration

struct readings {
  float reading[4];
};


long t;               //Time
int scale_factor[4];  //Array of scale factors for calibration
int stable_timer; //Incrimenter for num of loops before stable is set to true
float current_weight; //Current weight on mat
float load_cell[4];
readings previous, stable; //readings from previous loop, readings when weight becomes unstable

bool is_stable;



void setup() {
  int cal_counter=0;
  
  Serial.begin(38400);
  Serial.println("Wait...");
  
  t=0;
  is_stable = true;
  
  //Load cell tare
  for (int i = 0; i <4; i++)  { //Initialize scale factor and zero scale
      scale_factor[i] = 10;
      load_cell[i] = 0;
    }
  Serial.println("Startup + tare is complete");

  //Calibration
  float temp_reading = 0;
  Serial.println("Calibration beginning. Place 5lb at center of mat.");
    
    while (get_units(5,0) < WEIGHT_THRESHOLD) {  //Wait for weight to be put on mat
      delay(50); }
      Serial.println("Weight on mat");
      
    while (cal_counter < 10) {  //wait for sensor readings to stabilize
      if (abs(get_units(5, 0) - temp_reading) < WEIGHT_THRESHOLD)
        cal_counter++;
      delay(100);
      Serial.println(get_units(5,0));
      temp_reading = get_units(5, 0);
    }

    Serial.println("Readings stable");
    
    for (int i = 0; i <4; i++)  {   //Run calibration loop for all sensors
      load_cell[i] = get_units(2, i);
      while (abs(temp_reading - CALIBRATION_WEIGHT/4) > WEIGHT_THRESHOLD) //Loop until reading is close enough to actual value
      {
        temp_reading = get_units(2,i);
        if (temp_reading > CALIBRATION_WEIGHT/4 - WEIGHT_THRESHOLD/2)        //Increases scale factor if reading is above actual value
          scale_factor[i] += 1;
        else if (temp_reading < CALIBRATION_WEIGHT/4 - WEIGHT_THRESHOLD/2 && scale_factor[i] > 1)   //Decreases scale factor if reading is below actual value
          scale_factor[i] += -1;
          
        delay(100);
      }
    }
    for (int i = 0; i < 4; i++) {
      stable.reading[i] = get_units(2, i);
      previous.reading[i] = get_units(2,i);
    }
    
    Serial.println("Calibration done.");
}

void loop() {
  readings current, delta; //Current sensor readings and change in readings
  float stable_difference, recent_difference; //the difference in sensor readings since last period of stability and the difference between the current and last loop
  current_weight = 0;
  recent_difference = 0;
  stable_difference = 0;

  if (millis() > t+500) {
    //Serial.print("Current readings: ");
    for (int i = 0; i < 4; i++) { //Loop to get sensor readings and put them into readings array
      current.reading[i] = get_units(3,i);
      current_weight+=current.reading[i];
      delta.reading[i] = current.reading[i] - previous.reading[i];
      recent_difference+=delta.reading[i];
      stable_difference += current.reading[i] - stable.reading[i];
      Serial.print(stable.reading[i]);
      Serial.print(" ");
      previous.reading[i] = current.reading[i];
    }
    Serial.println();
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
        Serial.print("\t");
        for (int i = 0; i < 3; i++) {
          Serial.print(current.reading[i] - stable.reading[i]);
          Serial.print(",");
        }
        Serial.print(current.reading[3] - stable.reading[3]);
        Serial.println("\t");
        stable = current;
      }
    }
  t = millis();
  }
}

float get_units(int reading_amt, int sensor_num)
{
  float sum = 0;
  for (int i = 0; i<reading_amt; i++) {
    sum += analogRead(sensor_num);
  }
  return sum/(reading_amt*scale_factor[sensor_num]);
}


