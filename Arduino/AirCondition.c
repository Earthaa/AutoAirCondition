int motorPin = 4;
int motorPin1 = 7;

void setup()
{
 Serial.begin(9600);
 pinMode(motorPin,  OUTPUT);
 pinMode(motorPin1,  OUTPUT);
}
 
void loop()
{
  while(Serial.available())
   {
     char c=Serial.read();
      if(c=='A')
        {
          digitalWrite(motorPin, HIGH);
          digitalWrite(motorPin1, LOW);
        }
        else if(c=='B'){
          digitalWrite(motorPin, LOW);
          digitalWrite(motorPin1, HIGH);
        }
        else{
          digitalWrite(motorPin, LOW);
          digitalWrite(motorPin1, LOW);
        }
   }
}