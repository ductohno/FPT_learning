#define BLYNK_TEMPLATE_ID "xxxxx"
#define BLYNK_AUTH_TOKEN "xxxxx"
#include <BlynkSimpleEsp32.h>   

char auth[] = BLYNK_AUTH_TOKEN;
char ssid[] = "YourWiFi";
char pass[] = "YourPass";

// port of sensor and 5v relay
int moistSensor = 34;
int tempSensor = 35;
int relay = 9;

// constant
int minMoist = 30;
int maxMoist = 50;
int minTemp = 15;
int readSensorDelayPumpOn = 500;
int readSensorDelayPumpOff = 2000;
int displayDelay = 1000;

// status
int pumpOpen = 0;
int currentMoist = 0;
int currentTemp = 0;
unsigned long delayOfSensor = 0;
unsigned long tDisplay=0, tSensor=0 ,t;
bool isAutoMode = false;
bool manualInput = false;

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
  Blynk.virtualWrite(V4, pumpOpen);
}

void setup() {
  Serial.begin(9600);
  pinMode(moistSensor, INPUT);
  pinMode(tempSensor, INPUT);
  pinMode(relay, OUTPUT);
  digitalWrite(relay, pumpOpen);
  Blynk.begin(auth, ssid, pass);
}

void loop() {
  Blynk.run();
  // Receive data from sensor
  currentMoist = map(analogRead(moistSensor), 0, 1023, 0, 100);
  currentTemp = map(analogRead(tempSensor), 0, 1023, 0, 50);
  // Receive status
  t = millis();
  if(t-tDisplay >= displayDelay){
    sendToBlynk();
    Serial.print("Moist: "); Serial.print(currentMoist);
    Serial.print("% | Temp: "); Serial.print(currentTemp);
    Serial.print("°C | Pump: "); Serial.println(pumpOpen);
    tDisplay=t;
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
        if( currentTemp >= minTemp ){
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
    // open pump if manualInput is true
    if( manualInput == true){
      pumpOpen=1;
    }
    // close pump if manualInput is false
    else{
      pumpOpen=0;
    }
    digitalWrite(relay, pumpOpen);
  }
}
