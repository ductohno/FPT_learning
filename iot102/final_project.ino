#define BLYNK_TEMPLATE_ID ""
#define BLYNK_TEMPLATE_NAME "Watering automatic"
#define BLYNK_AUTH_TOKEN ""
#include <BlynkSimpleEsp32.h> 
#include <WiFi.h>  
#include <WiFiClient.h>
#include "DHT.h"

char ssid[] = "YourWiFi";
char pass[] = "YourPass";

// port of sensor and 5v relay
int moistSensor = 34;
int tempSensor = 33;
int relay = 25;

// constant
int minMoist = 30;
int maxMoist = 50;
int minTemp = 15;
int readSensorDelayPumpOn = 500;
int readSensorDelayPumpOff = 2000;
int manualDelay = 100;
int displayDelay = 1000;

// status
int pumpOpen = 0;
int currentMoist = 0;
int currentTemp = 0;
unsigned long delayOfSensor = 0;
unsigned long tDisplay=0, tSensor=0 ,tManual=0, t;
bool isAutoMode = false;
bool manualInput = false;

// DHT config
#define DHTTYPE DHT11    
DHT dht(tempSensor, DHTTYPE);

// read blynk button
BLYNK_WRITE(V0) { 
  isAutoMode = param.asInt();
}

BLYNK_WRITE(V1) { 
  manualInput = param.asInt();
}

// Send data to blynk
void sendToBlynk() {
  Blynk.virtualWrite(V2, currentMoist);
  Blynk.virtualWrite(V3, currentTemp);
  if(pumpOpen){
    Blynk.virtualWrite(V4, "ON");
  }
  else{
    Blynk.virtualWrite(V4, "OFF");
  }
}

void setup() {
  Serial.begin(9600);
  pinMode(moistSensor, INPUT);
  pinMode(tempSensor, INPUT);
  pinMode(relay, OUTPUT);
  digitalWrite(relay, pumpOpen);
  Blynk.begin(BLYNK_AUTH_TOKEN, ssid, pass);
}

void loop() {
  Blynk.run();
  // Receive data from sensor
  currentMoist = map(analogRead(moistSensor), 0, 4095, 0, 100);
  currentTemp = dht.readTemperature();

  // Receive status
  t = millis();
  if(t-tDisplay >= displayDelay){
    sendToBlynk();
    tDisplay=t;
    if (isnan(currentTemp)) {
      Blynk.virtualWrite(V4, "Temp sensor eror");
    }
  }
  // Auto mode or manual mode
  if(isAutoMode == true){
    // Decide the long of sensor delay
    if( pumpOpen == 1){
      delayOfSensor = readSensorDelayPumpOn;
    }
    else{
      delayOfSensor = readSensorDelayPumpOff;
    }
    if( t-tSensor >= delayOfSensor ){
      if( currentMoist <= minMoist || pumpOpen == 1){
        if( currentTemp > minTemp ){
          // If plant not enough moist, open pump
          if( currentMoist <= maxMoist ){
            pumpOpen=1;
          }
          // If enough, stop pump
          else{
            pumpOpen=0;
          }
        }
        else{
          // close the pump
          pumpOpen=0;
        }
      }
      else{
        // close the pump
        pumpOpen=0;
      }
      digitalWrite(relay, pumpOpen);
      tSensor = t;
    }
  }
  else{
    if(t-tManual > manualDelay){
      // open pump if manualInput is true
      if( manualInput == true){
        pumpOpen=1;
      }
      // close pump if manualInput is false
      else{
        pumpOpen=0;
      }
      digitalWrite(relay, pumpOpen);
      tManual = t;
    }
  }
}
