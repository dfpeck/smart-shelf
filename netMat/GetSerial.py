from threading import Thread
import NetClient
import serial

class GetSerial(Thread):
    def __init__(self):
        super(GetSerial, self).__init__()

    def run(self):
        ser = serial.Serial('/dev/ttyACM0', 38400)
        while 1:
            NetClient.send_string(ser.readline())





