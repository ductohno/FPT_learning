#define BLYNK_TEMPLATE_ID ""
#define BLYNK_TEMPLATE_NAME "Watering automatic"
#define BLYNK_AUTH_TOKEN ""

#include <BlynkSimpleEsp32.h>
#include <WiFi.h>
#include <WiFiClient.h>
#include "DHT.h"

// WiFi
char ssid[] = "AndroidAP";
char pass[] = "11111111";

// Sensor & Relay pins
int moistSensor = 34;
int tempSensor = 22;
int relay = 25;

// Constants
int minMoist = 30;
int maxMoist = 50;
float minTemp = 15.0;

// Status
int pumpOpen = 0;
int currentMoist = 0;
float currentTemp = 0;
int isAutoMode = 1;
int manualInput = 0;


// DHT config
#define DHTTYPE DHT11
DHT dht(tempSensor, DHTTYPE);

// Timer
BlynkTimer timer;

// ----------------- Functions -----------------

// Read sensors
void readSensor() {
  currentMoist = analogRead(moistSensor);
  currentMoist = map(currentMoist, 0, 4095, 100, 0); // 0=wet, 100=dry
  currentTemp = dht.readTemperature();

  Serial.print("Moisture %: "); Serial.print(currentMoist);
  Serial.print(" | Temperature: "); Serial.println(currentTemp);
  Serial.print("Pump status: "); Serial.println(pumpOpen);
}

// Send data to Blynk
void sendToBlynk() {
  Blynk.virtualWrite(V2, currentMoist);
  Blynk.virtualWrite(V3, currentTemp);
  Blynk.virtualWrite(V4, pumpOpen ? "PUMP ON" : "PUMP OFF");
}

// Automatic watering
void automaticWater() {
  if (isAutoMode == 1) {
    if (currentMoist < minMoist && currentTemp > minTemp) {
      pumpOpen = 1;
    } else if (currentMoist > maxMoist || currentTemp <= minTemp) {
      pumpOpen = 0;
    }
    digitalWrite(relay, pumpOpen);
  }
}

// ----------------- Blynk Handlers -----------------

BLYNK_WRITE(V0) { 
  isAutoMode = param.asInt();
  Serial.print("Auto mode: "); Serial.println(isAutoMode);
}

BLYNK_WRITE(V1) { // Manual control
  manualInput = param.asInt();
  Serial.print("Manual input: "); Serial.println(manualInput);

  if (isAutoMode == 0) {
    pumpOpen = manualInput ? 1 : 0;
    digitalWrite(relay, pumpOpen);
  }
}

void setup() {
  Serial.begin(115200);

  pinMode(moistSensor, INPUT);
  pinMode(tempSensor, INPUT);
  pinMode(relay, OUTPUT);
  digitalWrite(relay, pumpOpen);

  Blynk.begin(BLYNK_AUTH_TOKEN, ssid, pass);
  dht.begin();
  timer.setInterval(500L, readSensor);       
  timer.setInterval(2000L, sendToBlynk);   
  timer.setInterval(500L, automaticWater);   
}

void loop() {
  Blynk.run();
  timer.run();
}
