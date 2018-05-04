from threading import Thread
import NetClient
import serial

class GetSerial(Thread):
    def __init__(self, net_client):
        super(GetSerial, self).__init__()
        self.net_client = net_client

    def run(self):
        
        ser = serial.Serial('/dev/ttyACM0', 38400)
        while 1:
            self.net_client.send_string(ser.readline())





