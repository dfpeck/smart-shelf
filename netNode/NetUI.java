package netNode;

public class NetUI extends NetClient {
	public NetUI(String ip){
		super(ip);
		identity = "ui~";	
	}
}