import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

import java.io.IOException;
import java.util.Date;

public class TEST_pi4j_example {
	public static void main(String args[]) throws InterruptedException, IOException {
		final Console console = new Console();
		
		console.title("Arduino to Pi Test", "Test 1");
		
		console.promptForExit();
		
		final Serial serial = SerialFactory.createInstance();
		
		serial.addListener(new SerialDataEventListener() {
			@Override
			public void dataReceived(SerialDataEvent event) {

				// NOTE! - It is extremely important to read the data received from the
				// serial port.  If it does not get read from the receive buffer, the
				// buffer will continue to grow and consume memory.

				// print out the data received to the console
				try {
					console.println("[ASCII DATA] " + event.getAsciiString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		try {
			SerialConfig config = new SerialConfig();
			
			config.device(SerialPort.getDefaultPort())
				.baud(Baud._38400)
				.dataBits(DataBits._8)
				.parity(Parity.NONE)
				.stopBits(StopBits._1)
				.flowControl(FlowControl.NONE);
				
			if (args.length > 0){
				config = CommandArgumentParser.getSerialConfig(config, args);
		}
			
		console.box(" Connecting to: " + config.toString(),
				"Recieving data every 1 second.");
			
		
		try {serial.open(config);}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
			
		Thread me = Thread.currentThread();
		  synchronized (me)
		  {
			me.wait();
		  }
		}
		catch (SerialPortException ex)
		{
		  System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
		  return;
		}
  }
}