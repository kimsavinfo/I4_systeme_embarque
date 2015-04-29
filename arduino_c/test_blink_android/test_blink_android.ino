#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#define  LED_PIN  13
AndroidAccessory acc("Manufacturer",
		"Model",
		"Description",
		"1.0",
		"http://yoursite.com",
                "0000000012345678");
void setup()
{
  // set communiation speed
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);
  acc.powerOn();
}
 
void loop()
{
  byte msg[0];
  if (acc.isConnected()) 
  {
    // read data sended by Android
    int len = acc.read(msg, sizeof(msg), 1); 
    if (len > 0) 
    {
       if (msg[0] == 1)
       { 
          digitalWrite(LED_PIN,HIGH);
       }
       else
       {
          digitalWrite(LED_PIN,LOW);
       }
     }
  } 
  else
  {
    digitalWrite(LED_PIN , LOW);
  }
}
