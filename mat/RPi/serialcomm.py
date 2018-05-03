import serial

ser = serial.Serial('/dev/ttyACM0', 38400)

while 1 : NetClient.send_string(ser.readline())